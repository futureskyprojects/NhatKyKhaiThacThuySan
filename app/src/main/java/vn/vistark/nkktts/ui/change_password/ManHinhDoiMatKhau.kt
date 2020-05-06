package vn.vistark.nkktts.ui.change_password

import ChangePassSuccessResponse
import ForgotPasswordResponse
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import cn.pedant.SweetAlert.SweetAlertDialog
import kotlinx.android.synthetic.main.activity_man_hinh_doi_mat_khau.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.vistark.nkktts.R
import vn.vistark.nkktts.core.api.APIUtils
import vn.vistark.nkktts.core.constants.Constants
import vn.vistark.nkktts.utils.SimpleNotify
import vn.vistark.nkktts.utils.ToolbarBackButton
import java.lang.Exception

class ManHinhDoiMatKhau : AppCompatActivity() {
    lateinit var pDialog: SweetAlertDialog

    companion object {
        var isChangePassword = false
    }

    var theToken = ""
    var theTokenType = ""

    private fun initPreComponents() {
        // Progress dialog
        pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog.titleText = "Đang xử lý"
        pDialog.setCancelable(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_man_hinh_doi_mat_khau)

        initPreComponents()
        ToolbarBackButton(this).show()

        mhdkmPart2.visibility = View.GONE
        mhdmkBtnNutXacNhan.text = "Kiểm tra"
        if (isChangePassword) {
            title = "Đổi mật khẩu"
            mhdmkLnForgotPass.visibility = View.GONE
            mhdmkBtnNutXacNhan.text = "Hoàn tất"
            processingCheckBaseInfo(
                Constants.userInfo.username!!,
                Constants.userInfo.fishingLicense!!
            )
        } else {
            title = "Quên mật khẩu"
            mhdmkLnForgotPass.visibility = View.VISIBLE
            mhdmkBtnNutXacNhan.text = "KIỂM CHỨNG"
        }

        initEvents()
    }

    private fun initEvents() {
        mhdmkBtnNutXacNhan.setOnClickListener {
            val username = mhdmkEdtMaTaiKhoan.text.toString()
            val sdk = mhdmkEdtSoDangKyTau.text.toString()
            val pass1 = mhdmkEdtMatKhauMoi.text.toString()
            val pass2 = mhdmkEdtNhapLaiMatKhauMoi.text.toString()

            if (theToken.isNotEmpty() && theTokenType.isNotEmpty()) {
                if (pass1.isEmpty()) {
                    SimpleNotify.error(this, "MẬT KHẨU TRỐNG", "")
                } else if (pass1 != pass2) {
                    SimpleNotify.error(this, "MẬT KHẨU KHÔNG GIỐNG NHAU", "")
                } else {
                    changePass(pass1)
                }
            } else {
                processingCheckBaseInfo(username, sdk)
            }
        }
    }

    private fun changePass(pass: String) {
        if (!pDialog.isShowing) {
            pDialog.show()
        }
        APIUtils.getTempAPIServices(theTokenType, theToken)?.changePassword(pass)
            ?.enqueue(object : Callback<ChangePassSuccessResponse> {
                override fun onFailure(call: Call<ChangePassSuccessResponse>, t: Throwable) {
                    SimpleNotify.error(this@ManHinhDoiMatKhau, "LỖI MẠNG", "Vui lòng thử lại")
                }

                override fun onResponse(
                    call: Call<ChangePassSuccessResponse>,
                    response: Response<ChangePassSuccessResponse>
                ) {
                    if (response.isSuccessful) {
                        val changePassSuccessResponse = response.body()
                        if (changePassSuccessResponse != null) {
                            val token =
                                changePassSuccessResponse.changePassSuccessData.changePassSuccessToken.changePassSuccessOriginal.access_token
                            val tokenType =
                                changePassSuccessResponse.changePassSuccessData.changePassSuccessToken.changePassSuccessOriginal.token_type
                            if (token.isNotEmpty() && tokenType.isNotEmpty()) {
                                if (isChangePassword) {
                                    Constants.tokenType = tokenType
                                    Constants.updateTokenType()
                                    Constants.userToken = token
                                    Constants.updateUserToken()
                                }
                                Toast.makeText(
                                    this@ManHinhDoiMatKhau,
                                    "Đổi mật khẩu hoàn tất",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            }
                        }
                        SimpleNotify.success(this@ManHinhDoiMatKhau, "LỖI KHÔNG XÁC ĐỊNH", "")
                    } else {
                        SimpleNotify.success(this@ManHinhDoiMatKhau, "THẤT BẠI", "Vui lòng thử lại")
                    }
                }
            })
    }

    fun processingCheckBaseInfo(u: String, s: String) {
        if (u.isEmpty() || s.isEmpty()) {
            SimpleNotify.error(
                this@ManHinhDoiMatKhau,
                "THIẾU",
                "Vui lòng nhập tài khoản và số đăng ký tàu"
            )
            return
        }
        if (!pDialog.isShowing) {
            pDialog.show()
        }
        APIUtils.mAPIServices?.checkForgotPass(u, s)
            ?.enqueue(object : Callback<ForgotPasswordResponse> {
                override fun onFailure(call: Call<ForgotPasswordResponse>, t: Throwable) {
                    SimpleNotify.error(this@ManHinhDoiMatKhau, "LỖI MẠNG", "Vui lòng thử lại")
                    pDialog.dismissWithAnimation()
                }

                override fun onResponse(
                    call: Call<ForgotPasswordResponse>,
                    response: Response<ForgotPasswordResponse>
                ) {
                    if (response.isSuccessful) {
                        val forgotPasswordResponse = response.body()
                        if (forgotPasswordResponse != null) {
                            theTokenType =
                                forgotPasswordResponse.forgotData.forgotToken.forgotOriginal.token_type
                            theToken =
                                forgotPasswordResponse.forgotData.forgotToken.forgotOriginal.access_token
                            if (theTokenType.isNotEmpty() && theToken.isNotEmpty())
                                mhdmkBtnNutXacNhan.text = "Đổi mật khẩu"
                            mhdmkLnForgotPass.visibility = View.GONE
                            mhdkmPart2.visibility = View.VISIBLE
                            pDialog.dismissWithAnimation()
                            return
                        }
                    }
                    SimpleNotify.error(this@ManHinhDoiMatKhau, "LỖI", "Thông tin sai")
                    pDialog.dismissWithAnimation()
                }
            })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
