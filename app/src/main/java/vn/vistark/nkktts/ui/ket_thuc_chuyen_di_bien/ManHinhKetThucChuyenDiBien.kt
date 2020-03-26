package vn.vistark.nkktts.ui.ket_thuc_chuyen_di_bien

import PreviousTripNumberReponse
import SeaPortsReponse
import SyncSuccess
import TheTripStorage
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.man_hinh_ket_thuc_chuyen_di_bien.*
import kotlinx.android.synthetic.main.man_hinh_ket_thuc_chuyen_di_bien.mhktcdbLnChonCang
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.vistark.nkktts.R
import vn.vistark.nkktts.core.api.APIUtils
import vn.vistark.nkktts.core.constants.Constants
import vn.vistark.nkktts.core.constants.OfflineDataStorage
import vn.vistark.nkktts.core.models.trip_history.TripHistory
import vn.vistark.nkktts.ui.chon_cang.ManHinhChonCang
import vn.vistark.nkktts.ui.danh_sach_nghe.ManHinhDanhSachNghe
import vn.vistark.nkktts.ui.danh_sach_loai.ManHinhDanhSachLoai
import vn.vistark.nkktts.ui.khoi_tao_chuyen_di_bien.ManHinhKhoiTaoChuyenDiBien
import vn.vistark.nkktts.utils.DateTimeUtils
import vn.vistark.nkktts.utils.SimpleNotify
import vn.vistark.nkktts.utils.ToolbarBackButton

class ManHinhKetThucChuyenDiBien : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.man_hinh_ket_thuc_chuyen_di_bien)
        ToolbarBackButton(this).show()

        initEvents()
    }

    private fun initEvents() {
        mhktcdbLnChonCang.setOnClickListener {
            val intent = Intent(this, ManHinhChonCang::class.java)
            intent.putExtra(ManHinhChonCang.chonLoaiCang, ManHinhChonCang.cangVe)
            startActivityForResult(intent, ManHinhKhoiTaoChuyenDiBien.requestSeaPortCode)
        }

        mhktcdbBtnNopNhatKy.setOnClickListener {
            if (Constants.currentTrip.trip.destinationPort >= 0) {
                Constants.currentTrip.trip.captainId = Constants.userId.toInt()
                Constants.currentTrip.trip.destinationTime = DateTimeUtils.getStringCurrentYMD()
                Constants.currentTrip.trip.submitTime = DateTimeUtils.getStringCurrentYMD()
                //
                APIUtils.mAPIServices?.syncTrip(Constants.currentTrip)
                    ?.enqueue(object : Callback<SyncSuccess> {
                        override fun onFailure(call: Call<SyncSuccess>, t: Throwable) {
                            SimpleNotify.success(
                                this@ManHinhKetThucChuyenDiBien,
                                "CẦN KẾT NỐI INTERNET",
                                ""
                            )
                        }

                        override fun onResponse(
                            call: Call<SyncSuccess>,
                            response: Response<SyncSuccess>
                        ) {
                            TripHistory.add(Constants.currentTrip)
                            Constants.currentTrip = TheTripStorage()
                            Constants.updateCurrentTrip()
                            if (response.isSuccessful) {
                                Toast.makeText(
                                    this@ManHinhKetThucChuyenDiBien,
                                    "Đồng bộ chuyến thành công", Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    this@ManHinhKetThucChuyenDiBien,
                                    "Đồng bộ chưa được", Toast.LENGTH_SHORT
                                ).show()
                            }
                            // Về màn khởi tạo chuyến đi biển
                            val ktcdbIntent =
                                Intent(
                                    this@ManHinhKetThucChuyenDiBien,
                                    ManHinhKhoiTaoChuyenDiBien::class.java
                                )
                            startActivity(ktcdbIntent)
                            finish()
                        }
                    })

                Log.w("Token", GsonBuilder().create().toJson(Constants.userToken))
                Log.w("ABC", GsonBuilder().create().toJson(Constants.currentTrip))
            } else {
                SimpleNotify.error(this, "Chưa chọn cảng", "")
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        val manHinhDanhSachLoai = Intent(this, ManHinhDanhSachLoai::class.java)
        startActivity(manHinhDanhSachLoai)
        ToolbarBackButton(this).overrideAnimationOnEnterAndExitActivityReveret()
        finish()
        super.onBackPressed()
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

}
