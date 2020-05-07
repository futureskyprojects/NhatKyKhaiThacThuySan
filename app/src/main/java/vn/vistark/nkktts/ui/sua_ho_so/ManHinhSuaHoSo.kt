package vn.vistark.nkktts.ui.sua_ho_so

import CheckUser
import ProfileResponse
import UpdateProfileResponse
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.gson.Gson
import kotlinx.android.synthetic.main.man_hinh_khai_bao_so_dang_ky.*
import kotlinx.android.synthetic.main.man_hinh_sua_ho_so.*
import kotlinx.android.synthetic.main.man_hinh_sua_ho_so.mhkbsdkEdtNghePhu
import kotlinx.android.synthetic.main.man_hinh_sua_ho_so.mhkbsdkEdtSoGiayPhepKhaiThac
import kotlinx.android.synthetic.main.man_hinh_sua_ho_so.mhkbsdkTvThoiHan
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.vistark.nkktts.R
import vn.vistark.nkktts.core.api.APIUtils
import vn.vistark.nkktts.core.constants.Constants
import vn.vistark.nkktts.utils.SimpleNotify
import vn.vistark.nkktts.utils.ToolbarBackButton
import java.lang.Exception
import java.util.*

class ManHinhSuaHoSo : AppCompatActivity(), DatePickerDialog.OnDateSetListener {
    lateinit var pDialog: SweetAlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.man_hinh_sua_ho_so)
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

    private fun processing() {
        if (!pDialog.isShowing)
            pDialog.show()
    }

    fun processed() {
        if (pDialog.isShowing)
            pDialog.hide()
    }

    fun updateProfile() {
        val shipOwner: String = mhcctsnEdtParam1.text.toString()
        val captain: String = mhkbtthsEdtHoTenThuyenTruong.text.toString()
        val shipNumber: String = mhkbtthsEdtSoDangKyTau.text.toString()
        val lengthShip: String = mhkbtthsEdtCongSuatMayChinh.text.toString()
        val power: String = mhkbtthsEdtChieuDaiCuaTau.text.toString()
        val fishingLicense: String = mhkbsdkEdtSoGiayPhepKhaiThac.text.toString()
        val duration: String = mhkbsdkTvThoiHan.text.toString()
        val secondJob: String = mhkbsdkEdtNghePhu.text.toString()

        var isFully = true
        if (shipOwner.isBlank() || shipOwner.isEmpty()) {
            isFully = false
        } else if (captain.isBlank() || captain.isEmpty()) {
            isFully = false
        } else if (lengthShip.isBlank() || lengthShip.isEmpty()) {
            isFully = false
        } else if (power.isBlank() || power.isEmpty()) {
            isFully = false
        } else if (fishingLicense.isBlank() || fishingLicense.isEmpty()) {
            isFully = false
        } else if (duration.isBlank() || duration.isEmpty()) {
            isFully = false
        }

        if (power.toLongOrNull() == null || power.toLong() > Int.MAX_VALUE) {
            processed()
            SimpleNotify.error(
                this@ManHinhSuaHoSo,
                "Công suất máy không hợp lệ",
                "LỖI"
            )
            return
        }

        if (lengthShip.toLongOrNull() == null || lengthShip.toLong() > Int.MAX_VALUE) {
            processed()
            SimpleNotify.error(
                this@ManHinhSuaHoSo,
                "Chiều dài tàu không hợp lệ",
                "LỖI"
            )
            return
        }

        if (isFully) {
            Constants.userInfo.shipOwner = shipOwner
            Constants.userInfo.captain = captain
            Constants.userInfo.shipNumber = shipNumber
            Constants.userInfo.power = power
            Constants.userInfo.lengthShip = lengthShip
            Constants.userInfo.fishingLicense = fishingLicense
            Constants.userInfo.duration = duration
            Constants.userInfo.secondJob = secondJob

            APIUtils.mAPIServices?.profileUpdateAPI(Constants.userInfo)
                ?.enqueue(object : Callback<UpdateProfileResponse> {
                    override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                        t.printStackTrace()
                        SimpleNotify.error(
                            this@ManHinhSuaHoSo,
                            "Oops...",
                            "Không thể cập nhật!"
                        )
                        processed()
                    }

                    @SuppressLint("DefaultLocale")
                    override fun onResponse(
                        call: Call<UpdateProfileResponse>,
                        response: Response<UpdateProfileResponse>
                    ) {
                        if (response.code() == 200) {
                            SimpleNotify.success(
                                this@ManHinhSuaHoSo,
                                "HOÀN TẤT",
                                "Cập nhật thành công"
                            )
                        } else {
                            SimpleNotify.error(
                                this@ManHinhSuaHoSo,
                                "CHUA ĐƯỢC",
                                "Cập nhật không thành công"
                            )
                        }
                        processed()
                    }
                })

        } else {
            SimpleNotify.warning(
                this@ManHinhSuaHoSo,
                "THIẾU THÔNG TIN",
                "Vui lòng nhập đầy đủ các thông tin"
            )
        }
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
        mhshsHuy.setOnClickListener {
            onBackPressed()
        }
        mhshsHoanTat.setOnClickListener {
            //========== Tiến hành lấy giá trị nhập vào =====================//
            val shipNumber: String = mhkbtthsEdtSoDangKyTau.text.toString()
            //========== Xong lấy giá trị nhập vào =====================//
            if (shipNumber.isBlank() || shipNumber.isEmpty()) {
                SimpleNotify.warning(this, "THIẾU MÃ TÀU", "Vui lòng nhập số đăng ký tàu")
                return@setOnClickListener
            }
            processing()
            // Cập nhật hồ sơ ngay
            updateProfile()
//            // Tiến hành kiểm tra xem số tàu đã được đăng ký hay chưa
//            if (shipNumber != Constants.userInfo.shipNumber) {
//
//            } else {
//            }
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val pickedDate = String.format("%04d-%02d-%02d", year, month, dayOfMonth)
        mhkbsdkTvThoiHan.text = pickedDate
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
