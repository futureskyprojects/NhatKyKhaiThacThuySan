package vn.vistark.nkktts.ui.dang_nhap

import LoginFailResponse
import LoginResponse
import ProfileResponse
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.gson.Gson
import kotlinx.android.synthetic.main.man_hinh_dang_nhap.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.vistark.nkktts.R
import vn.vistark.nkktts.core.api.APIUtils
import vn.vistark.nkktts.core.constants.Constants
import vn.vistark.nkktts.ui.doi_mat_khau.ManHinhDoiMatKhau
import vn.vistark.nkktts.ui.danh_sach_nghe.ManHinhDanhSachNghe
import vn.vistark.nkktts.ui.khai_bao_thong_tin_ho_so.ManHinhKhaiBaoThongTinHoSo
import vn.vistark.nkktts.utils.SimpleNotify
import java.lang.Exception
import java.net.HttpURLConnection.HTTP_UNAUTHORIZED


class ManHinhDangNhap : AppCompatActivity() {
    lateinit var pDialog: SweetAlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // In Activity's onCreate() for instance
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val w = window
            w.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }
        setContentView(R.layout.man_hinh_dang_nhap)
        initPreComponents()
        initEvents()
        initPreValue()
    }

    private fun initPreValue() {
        if (Constants.sharedPreferences != null && Constants.readAllSavedData() && Constants.userInfo.username!!.isNotEmpty() && Constants.userInfo.password!!.isNotEmpty()) {
            mhdnEdtUsername.setText(Constants.userInfo.username)
            mhdnEdtPassword.setText(Constants.userInfo.password)
        }
    }

    private fun initPreComponents() {
        // Progress dialog
        pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog.titleText = "Đang xử lý"
        pDialog.setCancelable(false)
    }

    fun processing() {
        mhdnRegisterBtn.isEnabled = false
        mhdnLoginBtn.isEnabled = false
        if (!pDialog.isShowing)
            pDialog.show()
    }

    fun processed() {
        mhdnRegisterBtn.isEnabled = true
        mhdnLoginBtn.isEnabled = true
        if (pDialog.isShowing)
            pDialog.dismiss()
    }

    private fun initEvents() {
        mhdnTvForgetPassword.setOnClickListener {
            val intent = Intent(this, ManHinhDoiMatKhau::class.java)
            ManHinhDoiMatKhau.isChangePassword = false
            startActivity(intent)
        }

        mhdnRegisterBtn.setOnClickListener {
            chuyenQuaManHinhKhaiBaoThongTin()
            finish()
        }

        mhdnLoginBtn.setOnClickListener {
            processing()
            val maTaiKhoan = mhdnEdtUsername.text.toString()
            val matKhau = mhdnEdtPassword.text.toString()
            if (maTaiKhoan.isBlank() || maTaiKhoan.isEmpty() ||
                matKhau.isBlank() || matKhau.isEmpty()
            ) {
                SimpleNotify.warning(this, "NHẬP THIẾU", "Vui lòng nhập đầy đủ thông tin")
                processed()
                return@setOnClickListener
            } else {
                Constants.userInfo.username = maTaiKhoan
                Constants.userInfo.password = matKhau
                Constants.updateUserInfo()
                APIUtils.mAPIServices?.loginAPI(maTaiKhoan, matKhau)
                    ?.enqueue(object : Callback<LoginResponse> {
                        override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                            SimpleNotify.error(
                                this@ManHinhDangNhap,
                                "Oops...",
                                "Lỗi không xác định"
                            )
                            processed()
                        }

                        override fun onResponse(
                            call: Call<LoginResponse>,
                            response: Response<LoginResponse>
                        ) {
                            if (response.isSuccessful) {
                                val loginResponse = response.body()
                                if (loginResponse != null) {
                                    // Đăng nhập thành công, thiết lập lại API
                                    if (loginResponse.tokenType != null) {
                                        Constants.tokenType = loginResponse.tokenType
                                        Constants.updateTokenType()
                                    }
                                    if (loginResponse.accessToken != null) {
                                        Constants.userToken = loginResponse.accessToken
                                        Constants.updateUserToken()
                                    }
                                    APIUtils.replaceAPIServices()
                                    // Tiến hành lấy thông tin hồ sơ
                                    getUserProfile()
                                    // Hoàn tất
                                    return
                                }
                            } else if (response.code() == HTTP_UNAUTHORIZED) {
                                try {
                                    val errRes = response.errorBody()?.string()
                                    if (errRes != null) {
                                        val loginFailResponse =
                                            Gson().fromJson(errRes, LoginFailResponse::class.java)
                                        SimpleNotify.error(
                                            this@ManHinhDangNhap,
                                            "KHÔNG ĐƯỢC",
                                            "Sai tài khoản hoặc mật khẩu"
                                        )
                                        processed()
                                        return
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            SimpleNotify.error(
                                this@ManHinhDangNhap,
                                "ĐĂNG NHẬP THẤT BẠI",
                                "Đã xảy ra lỗi trong quá trình đăng nhập"
                            )
                            processed()
                        }
                    })
            }
        }
    }

    private fun getUserProfile() {
        APIUtils.mAPIServices?.profileAPI()?.enqueue(object : Callback<ProfileResponse> {
            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                SimpleNotify.error(
                    this@ManHinhDangNhap,
                    "LỖI LẤY HỒ SƠ",
                    "Không lấy được hồ sơ"
                )
                // Đăng xuất
                Constants.logOut()
                processed()
            }

            override fun onResponse(
                call: Call<ProfileResponse>,
                response: Response<ProfileResponse>
            ) {
                if (response.isSuccessful) {
                    val profileResponse = response.body()?.profile
                    if (profileResponse != null) {
                        Constants.userId = profileResponse.id.toString()
                        Constants.userInfo.shipOwner = profileResponse.shipOwner
                        Constants.userInfo.captain = profileResponse.captain
                        Constants.userInfo.shipNumber = profileResponse.shipNumber
                        Constants.userInfo.lengthShip = profileResponse.lengthShip.toString()
                        Constants.userInfo.power = profileResponse.power.toString()
                        Constants.userInfo.fishingLicense = profileResponse.fishingLicense
                        Constants.userInfo.duration = profileResponse.duration
                        Constants.userInfo.secondJob = profileResponse.secondJob
                        Constants.userInfo.phone = profileResponse.phone
                        Constants.userInfo.image = profileResponse.image
                        Constants.userInfo.status = profileResponse.status.toString()
                        Constants.userInfo.createAt = profileResponse.createdAt
                        Constants.userInfo.updateAt = profileResponse.updatedAt

                        Constants.updateUserInfo()
                        processed()
                        // Tiến hành vào trang chọn nghề
                        chuyenQuaManHinhDanhSachNghe()
                        finish()
                        //
                        return
                    }
                }
                SimpleNotify.error(
                    this@ManHinhDangNhap,
                    "HÃY ĐĂNG NHẬP LẠI",
                    "Không lấy được hồ sơ"
                )
                // Đăng xuất
                Constants.logOut()
                processed()
            }
        })
    }

    private fun chuyenQuaManHinhKhaiBaoThongTin() {
        startActivity(Intent(this, ManHinhKhaiBaoThongTinHoSo::class.java))
    }

    private fun chuyenQuaManHinhDanhSachNghe() {
        startActivity(Intent(this, ManHinhDanhSachNghe::class.java))
    }
}
