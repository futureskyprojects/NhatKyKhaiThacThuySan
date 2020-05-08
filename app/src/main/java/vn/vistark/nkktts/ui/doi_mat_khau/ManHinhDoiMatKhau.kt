package vn.vistark.nkktts.ui.doi_mat_khau

import ChangePassSuccessResponse
import ForgotPasswordResponse
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_man_hinh_doi_mat_khau.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.vistark.nkktts.R
import vn.vistark.nkktts.core.api.APIUtils
import vn.vistark.nkktts.core.constants.Constants
import vn.vistark.nkktts.utils.SimpleNotify
import vn.vistark.nkktts.utils.ToolbarBackButton

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
        pDialog.titleText = getString(R.string.dang_xu_ly)
        pDialog.setCancelable(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_man_hinh_doi_mat_khau)

        initPreComponents()
        ToolbarBackButton(this).show()

        mhdkmPart2.visibility = View.GONE
        mhdmkBtnNutXacNhan.text = getString(R.string.kiem_tra)
        if (isChangePassword) {
            title = getString(R.string.doi_mat_khau)
            mhdmkLnForgotPass.visibility = View.GONE
            mhdmkBtnNutXacNhan.text = getString(R.string.hoan_tat)
            processingCheckBaseInfo(
                Constants.userInfo.username!!,
                Constants.userInfo.fishingLicense!!
            )
        } else {
            title = getString(R.string.quen_mat_khau)
            mhdmkLnForgotPass.visibility = View.VISIBLE
            mhdmkBtnNutXacNhan.text = getString(R.string.kiem_chung).toUpperCase()
        }

        initEvents()
        supportActionBar?.title = getString(R.string.doi_mat_khau)
    }

    private fun initEvents() {
        mhdmkBtnNutXacNhan.setOnClickListener {
            val username = mhdmkEdtMaTaiKhoan.text.toString()
            val sdk = mhdmkEdtSoDangKyTau.text.toString()
            val pass1 = mhdmkEdtMatKhauMoi.text.toString()
            val pass2 = mhdmkEdtNhapLaiMatKhauMoi.text.toString()

            if (theToken.isNotEmpty() && theTokenType.isNotEmpty()) {
                if (pass1.isEmpty()) {
                    SimpleNotify.error(this, getString(R.string.mat_khau_trong).toUpperCase(), "")
                } else if (pass1 != pass2) {
                    SimpleNotify.error(
                        this,
                        getString(R.string.hai_mat_khau_khong_giong_nhau).toUpperCase(),
                        ""
                    )
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
                    SimpleNotify.error(
                        this@ManHinhDoiMatKhau,
                        getString(R.string.khong_co_mang).toUpperCase(),
                        getString(R.string.vui_long_thu_lai)
                    )
                }

                override fun onResponse(
                    call: Call<ChangePassSuccessResponse>,
                    response: Response<ChangePassSuccessResponse>
                ) {
                    if (response.isSuccessful) {
                        if (pDialog.isShowing) {
                            pDialog.dismiss()
                        }
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
                                    getString(R.string.doi_mat_khau_hoan_tat),
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                                return
                            }
                        }
                        SimpleNotify.success(
                            this@ManHinhDoiMatKhau,
                            getString(R.string.loi_khong_xac_dinh).toUpperCase(),
                            ""
                        )
                    } else {
                        if (pDialog.isShowing) {
                            pDialog.dismiss()
                        }
                        SimpleNotify.error(
                            this@ManHinhDoiMatKhau,
                            getString(R.string.doi_mat_khau_khong_thanh_cong).toUpperCase(),
                            ""
                        )
                    }
                }
            })
    }

    fun processingCheckBaseInfo(u: String, s: String) {
        if (u.isEmpty() || s.isEmpty()) {
            SimpleNotify.error(
                this@ManHinhDoiMatKhau,
                getString(R.string.thieu_thong_tin).toUpperCase(),
                getString(R.string.vui_long_nhap_tai_khoan_va_so_dang_ky_tau)
            )
            return
        }
        if (!pDialog.isShowing) {
            pDialog.show()
        }
        APIUtils.mAPIServices?.checkForgotPass(u, s)
            ?.enqueue(object : Callback<ForgotPasswordResponse> {
                override fun onFailure(call: Call<ForgotPasswordResponse>, t: Throwable) {
                    SimpleNotify.error(
                        this@ManHinhDoiMatKhau,
                        getString(R.string.khong_co_mang),
                        getString(R.string.vui_long_thu_lai)
                    )
                    pDialog.dismiss()
                }

                override fun onResponse(
                    call: Call<ForgotPasswordResponse>,
                    response: Response<ForgotPasswordResponse>
                ) {
                    if (response.isSuccessful) {
                        val forgotPasswordResponse = response.body()
                        println(GsonBuilder().create().toJson(response.body()))
                        println(GsonBuilder().create().toJson(response.errorBody()))
                        if (forgotPasswordResponse != null) {
                            theTokenType =
                                forgotPasswordResponse.forgotData.forgotToken.forgotOriginal.token_type
                            theToken =
                                forgotPasswordResponse.forgotData.forgotToken.forgotOriginal.access_token
                            if (theTokenType.isNotEmpty() && theToken.isNotEmpty())
                                mhdmkBtnNutXacNhan.text = getString(R.string.doi_mat_khau)
                            mhdmkLnForgotPass.visibility = View.GONE
                            mhdkmPart2.visibility = View.VISIBLE
                            pDialog.dismiss()
                            return
                        }
                    }
                    SimpleNotify.error(
                        this@ManHinhDoiMatKhau,
                        getString(R.string.thong_tin_sai),
                        getString(R.string.loi)
                    )
                    pDialog.dismiss()
                }
            })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        SimpleNotify.onBackConfirm(this) {
            finish()
            super.onBackPressed()
        }
    }
}
