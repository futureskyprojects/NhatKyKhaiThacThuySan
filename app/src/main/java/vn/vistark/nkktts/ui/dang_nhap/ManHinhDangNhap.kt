package vn.vistark.nkktts.ui.dang_nhap

import GetSelectedJobResponse
import LoginFailResponse
import LoginResponse
import ProfileResponse
import UpdateSelectedJobResponse
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.gson.Gson
import com.google.gson.GsonBuilder
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
import vn.vistark.nkktts.ui.khoi_dong.ManHinhKhoiDong
import vn.vistark.nkktts.ui.khoi_tao_chuyen_di_bien.ManHinhKhoiTaoChuyenDiBien
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
                                "KHÔNG CÓ MẠNG INTERNET",
                                "LỖI"
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
                        // Tiến hành vào trang chọn nghề
                        getSelectedJob()
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
        finish()
    }

    private fun chuyenQuaManHinhDanhSachNghe() {
        processed()
        startActivity(Intent(this, ManHinhDanhSachNghe::class.java))
        finish()
    }

    private fun getSelectedJob() {
        println("Tiến hành lấy nghề mà ngư dân đã chọn")
        APIUtils.mAPIServices?.getSelectedJobAPI()?.enqueue(object :
            Callback<GetSelectedJobResponse> {
            override fun onFailure(call: Call<GetSelectedJobResponse>, t: Throwable) {
                println("Bị lỗi khi lấy nghề")
                t.printStackTrace()
                chuyenQuaManHinhDanhSachNghe()
            }

            override fun onResponse(
                call: Call<GetSelectedJobResponse>,
                response: Response<GetSelectedJobResponse>
            ) {
                println(
                    "JOB GET SUCCESS: ${GsonBuilder().create().toJson(response.body())}"
                )
                println("JOB GET FAIL: ${GsonBuilder().create().toJson(response.errorBody())}")

                if (response.isSuccessful) {
                    processed()
                    val selectedJob = response.body()?.data?.first()
                    if (selectedJob != null) {
                        val jis = selectedJob.infoJob!!.replace("\\[|\\]".toRegex(), "").split(",")
                        Constants.selectedJob.id = selectedJob.id!!
                        Constants.selectedJob.jobId = selectedJob.jobId!!
                        Constants.selectedJob.jobInfoArray =
                            listOf(jis[0].trim().toFloat(), jis[1].trim().toFloat())
                        Constants.selectedJob.jobInfo = selectedJob.infoJob
                        Constants.selectedJob.captainId = selectedJob.captainId!!
                        Constants.updateSelectedJob()

                        val intent = Intent(this@ManHinhDangNhap, ManHinhKhoiDong::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                    Intent.FLAG_ACTIVITY_NEW_TASK
                        intent.putExtra("DONT_NEED_TO_LOAD_OFFLINE_DATAS", true)
                        startActivity(intent)
                    }
                } else {
                    println("Lấy nghề không thành công")
                    chuyenQuaManHinhDanhSachNghe()
                }
            }
        })
    }
}
