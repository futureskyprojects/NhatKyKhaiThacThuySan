package vn.vistark.nkktts.ui.khai_bao_so_dang_ky

import RegisterFail
import RegisterSuccess
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.gson.Gson
import kotlinx.android.synthetic.main.man_hinh_khai_bao_so_dang_ky.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.vistark.nkktts.R
import vn.vistark.nkktts.core.api.APIUtils
import vn.vistark.nkktts.core.api.RetrofitClient
import vn.vistark.nkktts.core.constants.Constants
import vn.vistark.nkktts.ui.danh_sach_nghe.ManHinhDanhSachNghe
import vn.vistark.nkktts.ui.khai_bao_thong_tin_ho_so.ManHinhKhaiBaoThongTinHoSo
import vn.vistark.nkktts.utils.SimpleNotify
import vn.vistark.nkktts.utils.ToolbarBackButton
import java.lang.Exception
import java.util.*


class ManHinhKhaiBaoSoDangKy : AppCompatActivity(), DatePickerDialog.OnDateSetListener {
    lateinit var pDialog: SweetAlertDialog
    var isEditingProfile = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.man_hinh_khai_bao_so_dang_ky)
        initPreComponents()
        ToolbarBackButton(this).show()
        initEvents()
        initPreviousData()
    }

    private fun initPreviousData() {
        if (Constants.sharedPreferences != null && Constants.readAllSavedData()) {
            mhkbsdkEdtSoGiayPhepKhaiThac.setText(Constants.userInfo.fishingLicense)
            mhkbsdkTvThoiHan.text = Constants.userInfo.duration
            mhkbsdkEdtNghePhu.setText(Constants.userInfo.secondJob)
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
        mhkbsdkBtnTiepTheo.isEnabled = false
        if (!pDialog.isShowing)
            pDialog.show()
    }

    fun processed() {
        mhkbsdkBtnTiepTheo.isEnabled = true
        if (pDialog.isShowing)
            pDialog.hide()
    }

    private fun initEvents() {
        // Chọn thời hạn
        mhkbsdkTvThoiHan.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                this,
                this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        // Nút tiếp theo
        mhkbsdkBtnTiepTheo.setOnClickListener {
            processing()

            val fishingLicense: String = mhkbsdkEdtSoGiayPhepKhaiThac.text.toString()
            val duration: String = mhkbsdkTvThoiHan.text.toString()
            val secondJob: String = mhkbsdkEdtNghePhu.text.toString()
            //============== Kiểm tra tính đầy đủ của dữ liệu ====================//
            var isFully = true
            if (fishingLicense.isBlank() || fishingLicense.isEmpty()) {
                isFully = false
            } else if (duration.isBlank() || duration.isEmpty()) {
                isFully = false
            }
            if (!isFully) {
                SimpleNotify.warning(
                    this,
                    "THIẾU THÔNG TIN",
                    "Vui lòng nhập đầy đủ các thông tin"
                )
                processed()
                return@setOnClickListener
            }
            //============== End tính đầy đủ của dữ liệu ====================//

            //============== Cập nhật vào thông tin đăng ký =================//
            Constants.userInfo.fishingLicense = fishingLicense
            Constants.userInfo.duration = duration
            Constants.userInfo.secondJob = secondJob
            //============== End Cập nhật vào TT đăng ký ====================//

            // Lưu lại thông tin
            Constants.updateUserInfo()

            APIUtils.mAPIServices?.registerAPI(Constants.userInfo)
                ?.enqueue(object : Callback<RegisterSuccess> {
                    override fun onFailure(call: Call<RegisterSuccess>, t: Throwable) {
                        SimpleNotify.error(
                            this@ManHinhKhaiBaoSoDangKy,
                            "Oops...",
                            "Không thể đăng ký, hãy thử lại"
                        )
                        processed()
                    }

                    @SuppressLint("DefaultLocale")
                    override fun onResponse(
                        call: Call<RegisterSuccess>,
                        response: Response<RegisterSuccess>
                    ) {
                        if (response.code() == 200 || response.code() == 419) {
                            if (response.code() == 200) {
                                val registerSuccess = response.body()
                                if (registerSuccess != null) {
                                    if (registerSuccess.status == 200 && registerSuccess.data != null) {
                                        Constants.userId = registerSuccess.data.id.toString()
                                        Constants.updateUserId()
                                        if (registerSuccess.data.token?.original?.access_token != null) {
                                            Constants.userToken =
                                                registerSuccess.data.token.original.access_token
                                            Constants.updateUserToken()
                                        }

                                        if (registerSuccess.data.token?.original?.token_type != null) {
                                            Constants.tokenType =
                                                registerSuccess.data.token.original.token_type
                                            Constants.updateTokenType()
                                        }
                                        processed()
                                        //========== ĐĂNG KÝ THÀNH CÔNG ========//
                                        if (!isEditingProfile) {
                                            APIUtils.replaceAPIServices()
                                        }
                                        chuyenQuaManHinhDanhSachNghe()
                                        finish()
                                        //========== END ĐK THÀNH CÔNG =========//
                                        return
                                    }
                                }
                            } else {
                                try {
                                    val errorRes = response.errorBody()?.string()
                                    if (errorRes != null) {
                                        val registerFail =
                                            Gson().fromJson(errorRes, RegisterFail::class.java)
                                        if (registerFail?.message != null && registerFail.errors != null) {
                                            SimpleNotify.warning(
                                                this@ManHinhKhaiBaoSoDangKy,
                                                registerFail.message.toUpperCase(),
                                                if (!registerFail.errors.username.isNullOrEmpty()) registerFail.errors.username[0] else "Số đăng ký tàu đã được đăng ký"
                                            )
                                            processed()
                                            return
                                        }
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }

                            SimpleNotify.error(
                                this@ManHinhKhaiBaoSoDangKy,
                                "LỖI MÁY CHỦ",
                                "Đăng ký không thành công"
                            )
                            processed()
                            return
                        }
                        println("Lỗi không xác định, Mã: ${response.code()}")
                        SimpleNotify.error(
                            this@ManHinhKhaiBaoSoDangKy,
                            "Oops...",
                            "Lỗi không xác định"
                        )
                        processed()
                    }
                })
        }
    }

    private fun chuyenQuaManHinhDanhSachNghe() {
        startActivity(Intent(this, ManHinhDanhSachNghe::class.java))
    }

    //==================== KHU VỰC OVERRIDE PHƯƠNG THỨC =========================//
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        val manHinhKhaiBaoThongTinHoSoIntent = Intent(this, ManHinhKhaiBaoThongTinHoSo::class.java)
        startActivity(
            manHinhKhaiBaoThongTinHoSoIntent
        )
        ToolbarBackButton(this).overrideAnimationOnEnterAndExitActivityReveret()
        finish()
        super.onBackPressed()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val pickedDate = String.format("%04d-%02d-%02d", year, month, dayOfMonth)
        mhkbsdkTvThoiHan.text = pickedDate
    }

    //==================== END OVERRIDE PHƯƠNG THỨC =========================//
}
