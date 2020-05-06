package vn.vistark.nkktts.ui.khai_bao_thong_tin_ho_so

import CheckUser
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.gson.Gson
import kotlinx.android.synthetic.main.man_hinh_khai_bao_thong_tin_ho_so.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.vistark.nkktts.R
import vn.vistark.nkktts.core.api.APIUtils
import vn.vistark.nkktts.core.constants.Constants
import vn.vistark.nkktts.ui.khai_bao_so_dang_ky.ManHinhKhaiBaoSoDangKy
import vn.vistark.nkktts.ui.dang_nhap.ManHinhDangNhap
import vn.vistark.nkktts.utils.SimpleNotify
import vn.vistark.nkktts.utils.ToolbarBackButton
import java.lang.Exception


class ManHinhKhaiBaoThongTinHoSo : AppCompatActivity() {
    lateinit var pDialog: SweetAlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.man_hinh_khai_bao_thong_tin_ho_so)
        initPreComponents()
        ToolbarBackButton(this).show()
        initEvents()
        initPreviousSavedData()
    }

    private fun initPreviousSavedData() {
        if (Constants.sharedPreferences != null && Constants.readAllSavedData()) {
            mhcctsnEdtParam1.setText(Constants.userInfo.shipOwner)
            mhkbtthsEdtHoTenThuyenTruong.setText(Constants.userInfo.captain)
            mhkbtthsEdtSoDangKyTau.setText(Constants.userInfo.shipNumber)
            mhkbtthsEdtCongSuatMayChinh.setText(Constants.userInfo.power)
            mhkbtthsEdtChieuDaiCuaTau.setText(Constants.userInfo.lengthShip)
        }
    }

    private fun initPreComponents() {
        // Progress dialog
        pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog.titleText = "Đang xử lý"
        pDialog.setCancelable(false)
    }

    private fun processing() {
        mhkbtthsBtnTiepTheo.isEnabled = false
        if (!pDialog.isShowing)
            pDialog.show()
    }

    fun processed() {
        mhkbtthsBtnTiepTheo.isEnabled = true
        if (pDialog.isShowing)
            pDialog.hide()
    }

    private fun initEvents() {
        mhkbtthsBtnTiepTheo.setOnClickListener {
            //========== Tiến hành lấy giá trị nhập vào =====================//
            val shipOwner: String = mhcctsnEdtParam1.text.toString()
            val captain: String = mhkbtthsEdtHoTenThuyenTruong.text.toString()
            val shipNumber: String = mhkbtthsEdtSoDangKyTau.text.toString()
            val lengthShip: String = mhkbtthsEdtCongSuatMayChinh.text.toString()
            val power: String = mhkbtthsEdtChieuDaiCuaTau.text.toString()
            //========== Xong lấy giá trị nhập vào =====================//

            if (shipNumber.isBlank() || shipNumber.isEmpty()) {
                SimpleNotify.warning(this, "THIẾU THÔNG TIN", "Vui lòng nhập số đăng ký tàu")
                return@setOnClickListener
            }
            processing()
            // Tiến hành kiểm tra xem số tàu đã được đăng ký hay chưa
            APIUtils.mAPIServices?.checkUserAPI(shipNumber)?.enqueue(object : Callback<CheckUser> {
                override fun onFailure(call: Call<CheckUser>, t: Throwable) {
                    SimpleNotify.error(
                        this@ManHinhKhaiBaoThongTinHoSo,
                        "Oops...",
                        "Không thể kiểm tra được số đăng ký tàu"
                    )
                    println("Không thể kiểm tra được")
                    processed()
                }

                override fun onResponse(call: Call<CheckUser>, response: Response<CheckUser>) {
                    processed()
                    if (response.code() == 200 || response.code() == 419) {
                        var checkUser: CheckUser? = null
                        if (response.code() == 200) {
                            checkUser = response.body()
                        } else {
                            try {
                                val errResponse = response.errorBody()?.string()
                                if (errResponse != null) {
                                    println(errResponse)
                                    checkUser = Gson().fromJson(errResponse, CheckUser::class.java)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()

                            }
                        }
                        if (checkUser != null) {
                            if (checkUser.status == 200) {
                                // Tiến hành kiểm tra các thông số còn lại
                                var isFully = true
                                if (shipOwner.isBlank() || shipOwner.isEmpty()) {
                                    isFully = false
                                } else if (captain.isBlank() || captain.isEmpty()) {
                                    isFully = false
                                } else if (lengthShip.isBlank() || lengthShip.isEmpty()) {
                                    isFully = false
                                } else if (power.isBlank() || power.isEmpty()) {
                                    isFully = false
                                }
                                if (isFully) {
                                    // Nhập thông tin vào cho thông tin người dùng
                                    Constants.userInfo.username = shipNumber
                                    Constants.userInfo.password = "123456"
                                    Constants.userInfo.shipOwner = shipOwner
                                    Constants.userInfo.captain = captain
                                    Constants.userInfo.shipNumber = shipNumber
                                    Constants.userInfo.power = power
                                    Constants.userInfo.lengthShip = lengthShip
                                    // Lưu lại thông tin
                                    Constants.updateUserInfo()
                                    // Kết thúc nhập thông tin vào cho người dùng
                                    chuyenQuaManHinhSoDangKy()
                                    finish()
                                } else {
                                    SimpleNotify.warning(
                                        this@ManHinhKhaiBaoThongTinHoSo,
                                        "THIẾU THÔNG TIN",
                                        "Vui lòng nhập đầy đủ các thông tin"
                                    )
                                }
                            } else {
                                SimpleNotify.warning(
                                    this@ManHinhKhaiBaoThongTinHoSo,
                                    "BỊ TRÙNG",
                                    "Số đăng ký tàu đã tồn tại"
                                )
                            }
                            return
                        } else {
                            SimpleNotify.error(
                                this@ManHinhKhaiBaoThongTinHoSo,
                                "Oops...",
                                "Lỗi khi kiểm tra số đăng ký tàu"
                            )
                            return
                        }
                    }
                    println("Lỗi không xác định, Mã: ${response.code()}")
                    SimpleNotify.error(
                        this@ManHinhKhaiBaoThongTinHoSo,
                        "Oops...",
                        "Lỗi không xác định"
                    )
                }
            })
        }
    }

    private fun chuyenQuaManHinhSoDangKy() {
        startActivity(Intent(this, ManHinhKhaiBaoSoDangKy::class.java))
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        val intent = Intent(this, ManHinhDangNhap::class.java)
        startActivity(
            intent
        )
        ToolbarBackButton(this).overrideAnimationOnEnterAndExitActivityReveret()
        finish()
        super.onBackPressed()
    }

}
