package vn.vistark.nkktts.ui.ket_thuc_chuyen_di_bien

import PreviousTripNumberReponse
import ProfileResponse
import SeaPortsReponse
import SyncSuccess
import TheTripStorage
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
import vn.vistark.nkktts.core.models.trip_history.HistoryTripSuccessResponse
import vn.vistark.nkktts.core.models.trip_history.TripHistory
import vn.vistark.nkktts.core.models.upload_image.UploadImageSuccessResponse
import vn.vistark.nkktts.ui.chon_cang.ManHinhChonCang
import vn.vistark.nkktts.ui.danh_sach_nghe.ManHinhDanhSachNghe
import vn.vistark.nkktts.ui.danh_sach_loai.ManHinhDanhSachLoai
import vn.vistark.nkktts.ui.khoi_tao_chuyen_di_bien.ManHinhKhoiTaoChuyenDiBien
import vn.vistark.nkktts.utils.DateTimeUtils
import vn.vistark.nkktts.utils.SimpleNotify
import vn.vistark.nkktts.utils.ToolbarBackButton
import java.io.File
import java.lang.Exception
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class ManHinhKetThucChuyenDiBien : AppCompatActivity() {
    lateinit var pDialog: SweetAlertDialog
    private fun initPreComponents() {
        // Progress dialog
        pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog.titleText = "Đang xử lý"
        pDialog.setCancelable(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.man_hinh_ket_thuc_chuyen_di_bien)
        initPreComponents()
        ToolbarBackButton(this).show()

        initEvents()
    }

    private fun errNotify() {
        SimpleNotify.error(this, "Oops...", "Vui lòng thử lại")
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
                                if (response.isSuccessful) {
                                    val profileResponse = response.body()?.profile
                                    if (profileResponse != null) {
                                        Constants.userId = "${profileResponse.id}"
                                        Constants.updateUserId()
                                        syncHistoryTrip()
                                        return
                                    }
                                }
                                pDialog.dismiss()
                                errNotify()

                            }
                        })
                    return@setOnClickListener
                }
                syncHistoryTrip()
            } else {
                SimpleNotify.error(this, "Chưa chọn cảng", "")
            }
        }
    }

    private fun syncHistoryTrip() {
        // Tiến hành lấy lịch sử chuyến đi
        APIUtils.mAPIServices?.getHistoryTrip()
            ?.enqueue(object : Callback<HistoryTripSuccessResponse> {
                override fun onFailure(call: Call<HistoryTripSuccessResponse>, t: Throwable) {
                    SimpleNotify.success(
                        this@ManHinhKetThucChuyenDiBien,
                        "CẦN KẾT NỐI INTERNET",
                        ""
                    )
//                    if (OfflineDataStorage.get<SeaPortsReponse>(OfflineDataStorage.tripHistory) == null) {
//                        SimpleNotify.success(
//                            this@ManHinhKetThucChuyenDiBien,
//                            "CẦN KẾT NỐI INTERNET",
//                            ""
//                        )
//                    } else {
//                        println("LOG: Tiến hành lấy dữ liệu lịch sử của chuyến đi cũ")
//                    }
                }

                @SuppressLint("SimpleDateFormat")
                override fun onResponse(
                    call: Call<HistoryTripSuccessResponse>,
                    response: Response<HistoryTripSuccessResponse>
                ) {
                    if (response.isSuccessful) {
                        val htripDatas = response.body()?.data
                        if (htripDatas != null) {
                            // Lưu dữ liệu lại, cũng là cập nhật mới
                            OfflineDataStorage.saveData(
                                OfflineDataStorage.tripHistory,
                                htripDatas
                            )
                            // Coppy để lấy các chuyến cùng năm
                            var tempHTripDatas =
                                emptyArray<HistoryTripSuccessResponse.HtripData>()
                            htripDatas.forEach {
                                val time =
                                    it.submit_time ?: it.destination_time ?: it.created_at
                                val y = time.subSequence(0, 4).toString().toIntOrNull()
                                if (y != null && y >= Calendar.getInstance()
                                        .get(Calendar.YEAR)
                                ) {
                                    tempHTripDatas = tempHTripDatas.plus(it)
                                }
                            }
                            // Sắp xếp lại mảng để có danh sách lịch sử chuyến với trip_number giảm dần
                            tempHTripDatas =
                                tempHTripDatas.sortedByDescending { it.trip_number.toInt() }
                                    .toTypedArray()
                            // Nếu dữ liệu trống thì khởi đầu là 1,
                            // Còn không thì lấy trip_number lớn nhất + 1
                            // Tiến hành đồng bộ ảnh trước
                            if (tempHTripDatas.isEmpty()) {
                                syncImages(1)
                            } else {
                                syncImages(tempHTripDatas.first().trip_number.toInt() + 1)
                            }
                        }
                    }
                }
            })
    }

    private fun syncImages(currentTripId: Int) {
        // Tiến hành đồng bộ ảnh
        for (i in Constants.currentTrip.trip.hauls.indices) {
            for (j in Constants.currentTrip.trip.hauls[i].spices.indices) {
                val imgArr = GsonBuilder().create()
                    .fromJson(
                        Constants.currentTrip.trip.hauls[i].spices[j].images,
                        Array<String>::class.java
                    )
                for (k in imgArr) {
                    val f = File(k)
                    if (f.exists()) {
                        val imageFileBody = RequestBody.create(
                            MediaType.parse("image/jpeg"),
                            f
                        )

                        try {
                            // Gửi yêu cầu và lấy phản hồi
                            val res = APIUtils.mAPIServices?.uploadImage(
                                MultipartBody.Part.createFormData(
                                    "image",
                                    f.name,
                                    imageFileBody
                                )
                            )?.execute()

                            // Xử lý phản hồi
                            if (res != null && res.isSuccessful) {
                                val path = res.body()?.result?.path
                                if (path != null) {
                                    do {
                                        try {
                                            Constants.currentTrip.trip.hauls[i].spices[j].images =
                                                Constants.currentTrip.trip.hauls[i].spices[j].images.replace(
                                                    k,
                                                    path
                                                )
                                            return
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    } while (true)
                                }
                            } else {
                                println("ERROR: Không may, kết quả trả về khi đồng bộ ảnh bị null hoặc không thành công")
                            }
                        } catch (ez: Exception) {
                            ez.printStackTrace()
                        }
                    }
                }
            }
        }
        // Sau khi đồng bộ xong thì tiến hành gửi ảnh lên server
        syncCurrentTrip(currentTripId)
    }

    private fun syncCurrentTrip(currentTripId: Int) {
        Constants.currentTrip.trip.captainId = Constants.userId.toInt()
        Constants.currentTrip.trip.destinationTime = DateTimeUtils.getStringCurrentYMD()
        Constants.currentTrip.trip.submitTime = DateTimeUtils.getStringCurrentYMD()
        Constants.currentTrip.trip.tripNumber = currentTripId

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

}
