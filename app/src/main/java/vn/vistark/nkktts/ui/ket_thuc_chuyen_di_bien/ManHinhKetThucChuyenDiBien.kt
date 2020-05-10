package vn.vistark.nkktts.ui.ket_thuc_chuyen_di_bien

import ProfileResponse
import SeaPortsReponse
import SyncSuccess
import TheTripStorage
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.man_hinh_ket_thuc_chuyen_di_bien.*
import kotlinx.android.synthetic.main.man_hinh_ket_thuc_chuyen_di_bien.mhktcdbLnChonCang
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.vistark.nkktts.R
import vn.vistark.nkktts.core.api.APIUtils
import vn.vistark.nkktts.core.constants.Constants
import vn.vistark.nkktts.core.constants.OfflineDataStorage
import vn.vistark.nkktts.core.db.TripWaitForSync
import vn.vistark.nkktts.core.models.trip_history.HistoryTripSuccessResponse
import vn.vistark.nkktts.ui.chon_cang.ManHinhChonCang
import vn.vistark.nkktts.ui.danh_sach_loai.ManHinhDanhSachLoai
import vn.vistark.nkktts.ui.khoi_tao_chuyen_di_bien.ManHinhKhoiTaoChuyenDiBien
import vn.vistark.nkktts.utils.DateTimeUtils
import vn.vistark.nkktts.utils.SimpleNotify
import vn.vistark.nkktts.utils.ToolbarBackButton
import java.io.File
import java.lang.Exception
import java.util.*

class ManHinhKetThucChuyenDiBien : AppCompatActivity() {
    lateinit var pDialog: SweetAlertDialog
    private fun initPreComponents() {
        // Progress dialog
        pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog.titleText = getString(R.string.dang_xu_ly)
        pDialog.setCancelable(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.man_hinh_ket_thuc_chuyen_di_bien)
        initPreComponents()
        ToolbarBackButton(this).show()

        initEvents()
        supportActionBar?.title = getString(R.string.ket_thuc_chuyen_di_bien)
    }

    private fun errNotify() {
        SimpleNotify.error(this, "Oops...", getString(R.string.vui_long_thu_lai))
    }

    private fun initEvents() {
        mhktcdbLnChonCang.setOnClickListener {
            val intent = Intent(this, ManHinhChonCang::class.java)
            intent.putExtra(ManHinhChonCang.chonLoaiCang, ManHinhChonCang.cangVe)
            startActivityForResult(intent, ManHinhKhoiTaoChuyenDiBien.requestSeaPortCode)
        }

        mhktcdbBtnNopNhatKy.setOnClickListener {
            if (!pDialog.isShowing) {
                pDialog.show()
            }
            if (Constants.currentTrip.trip.destinationPort >= 0) {
                if (Constants.userId == "") {
                    APIUtils.mAPIServices?.profileAPI()
                        ?.enqueue(object : Callback<ProfileResponse> {
                            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                                pDialog.dismiss()
                                errNotify()
                            }

                            override fun onResponse(
                                call: Call<ProfileResponse>,
                                response: Response<ProfileResponse>
                            ) {
                                pDialog.dismiss()
                                if (response.isSuccessful) {
                                    val profileResponse = response.body()?.profile
                                    if (profileResponse != null) {
                                        Constants.userId = "${profileResponse.id}"
                                        Constants.updateUserId()
//                                        syncHistoryTrip()
                                        saveToDatabase()
                                        return
                                    }
                                }
                                errNotify()

                            }
                        })
                    return@setOnClickListener
                }
//                syncHistoryTrip()
                saveToDatabase()
            } else {
                pDialog.dismiss()
                SimpleNotify.error(this, getString(R.string.chua_chon_cang).toUpperCase(), "")
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        SimpleNotify.onBackConfirm(this) {
            val manHinhDanhSachLoai = Intent(this, ManHinhDanhSachLoai::class.java)
            startActivity(manHinhDanhSachLoai)
            ToolbarBackButton(this).overrideAnimationOnEnterAndExitActivityReveret()
            finish()
            super.onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == ManHinhKhoiTaoChuyenDiBien.requestSeaPortCode) {
            if (resultCode == Activity.RESULT_OK) {
                showPreviousSelectedSeaPort()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun showPreviousSelectedSeaPort() {
        if (Constants.currentTrip.trip.destinationPort >= 0) {
            val seaPortsReponse =
                OfflineDataStorage.get<SeaPortsReponse>(OfflineDataStorage.seaPorts)
            if (seaPortsReponse?.seaPorts != null) {
                mhktcdbIvIcon.setImageResource(R.drawable.ic_pin)
                for (seaPort in seaPortsReponse.seaPorts) {
                    if (seaPort.id == Constants.currentTrip.trip.destinationPort) {
                        mhktcdbTvTenCang.text = seaPort.name
                        return
                    }
                }
            }
        }
    }

    private fun saveToDatabase() {
        // Lưu dữ liệu chuyến đi này vào CSDL
        Constants.currentTrip.trip.captainId = Constants.userId.toInt()
        Constants.currentTrip.trip.destinationTime = DateTimeUtils.getStringCurrentYMD()
        Constants.currentTrip.trip.submitTime = DateTimeUtils.getStringCurrentYMD()
        TripWaitForSync(this).add(Constants.currentTrip)
        // Xóa dữ liệu chuyến đi để tạo chuyến đi mới
        Constants.currentTrip = TheTripStorage()
        Constants.updateCurrentTrip()
        // Đến màn hình tạo chuyến đi mới
        val ktcdbIntent =
            Intent(
                this@ManHinhKetThucChuyenDiBien,
                ManHinhKhoiTaoChuyenDiBien::class.java
            )
        startActivity(ktcdbIntent)
        // Kết thúc màn hình hiện tại
        finish()
        // Thông báo đến người dùng
        Toast.makeText(
            this,
            getString(R.string.chuyen_di_nay_se_duoc_dong_bo_ngam),
            Toast.LENGTH_SHORT
        ).show()
    }
}
