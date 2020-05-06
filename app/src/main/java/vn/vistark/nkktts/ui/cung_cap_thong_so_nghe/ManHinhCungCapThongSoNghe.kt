package vn.vistark.nkktts.ui.cung_cap_thong_so_nghe

import UpdateSelectedJobResponse
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.man_hinh_cung_cap_thong_so_nghe.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.vistark.nkktts.R
import vn.vistark.nkktts.core.api.APIUtils
import vn.vistark.nkktts.core.constants.Constants
import vn.vistark.nkktts.ui.danh_sach_nghe.ManHinhDanhSachNghe
import vn.vistark.nkktts.ui.khoi_tao_chuyen_di_bien.ManHinhKhoiTaoChuyenDiBien
import vn.vistark.nkktts.utils.SimpleNotify
import vn.vistark.nkktts.utils.ToolbarBackButton

class ManHinhCungCapThongSoNghe : AppCompatActivity() {

    companion object {
        var jobId: Int = -1
    }

    var actRes = Activity.RESULT_CANCELED

    lateinit var pDialog: SweetAlertDialog

    var inputTitle1 = ""
    var inputTitle2 = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.man_hinh_cung_cap_thong_so_nghe)

        ToolbarBackButton(this).show()
        initPreComponents()
        initInputTitle()
        updateInputTitleAndPlaceHolder()

        initEvents()

        if (Constants.selectedJob.jobId == jobId) {
            if (Constants.selectedJob.jobInfoArray.size == 2) {
                mhcctsnEdtParam1.setText(Constants.selectedJob.jobInfoArray[0].toString())
                mhcctsnEdtParam2.setText(Constants.selectedJob.jobInfoArray[1].toString())
            }
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
        if (!pDialog.isShowing)
            pDialog.show()
        mhcctsnBtnHoanThanh.isEnabled = false
    }

    fun processed() {
        if (pDialog.isShowing)
            pDialog.hide()
        mhcctsnBtnHoanThanh.isEnabled = true
    }

    private fun updateInputTitleAndPlaceHolder() {
        mhcctsnTvTitle1.text = inputTitle1
        mhcctsnEdtParam1.hint = inputTitle1

        mhcctsnTvTitle2.text = inputTitle2
        mhcctsnEdtParam2.hint = inputTitle2
    }

    private fun initInputTitle() {
        when (jobId) {
            1 -> {
                inputTitle1 = "Chiều dài toàn bộ vàng câu"
                inputTitle2 = "Số lưỡi câu"
            }
            2 -> {
                inputTitle1 = "Chiều dài toàn bộ lưới"
                inputTitle2 = "Chiều cao lưới"
            }
            3 -> {
                inputTitle1 = "Chu vi miệng lưới"
                inputTitle2 = "Chiều cao lưới"
            }
            else -> {
                inputTitle1 = "Chiều dài giềng phao"
                inputTitle2 = "Chiều dài toàn bộ lưới"
            }
        }
    }

    private fun initEvents() {
        mhcctsnBtnHoanThanh.setOnClickListener {
            processing()
            // Lấy thông số đầu vào
            val param1 = mhcctsnEdtParam1.text.toString()
            val param2 = mhcctsnEdtParam2.text.toString()
            // Cập nhật
            if (param1.isEmpty() || param2.isEmpty()) {
                SimpleNotify.warning(
                    this,
                    "THIẾU THÔNG TIN",
                    "Vui lòng nhập đầy đủ các thông tin"
                )
                processed()
            } else {
                if (!ManHinhDanhSachNghe.isEdit) {
                    Constants.selectedJob.jobId = jobId
                    Constants.selectedJob.jobInfoArray = listOf(param1.toFloat(), param2.toFloat())
                    Constants.selectedJob.jobInfo =
                        GsonBuilder().create().toJson(Constants.selectedJob.jobInfoArray)
                    updateSelectedJob()
                } else {
                    changeSelectedJob(
                        jobId.toString(),
                        GsonBuilder().create().toJson(listOf(param1.toFloat(), param2.toFloat()))
                    )
                }
            }
        }
    }

    private fun changeSelectedJob(jobId: String, infoJob: String) {
        Log.w("AXASXSAX", infoJob)
        APIUtils.mAPIServices?.changeSelectedJobAPI(jobId, infoJob)?.enqueue(object :
            Callback<UpdateSelectedJobResponse> {
            override fun onFailure(call: Call<UpdateSelectedJobResponse>, t: Throwable) {
                processed()
                SimpleNotify.error(
                    this@ManHinhCungCapThongSoNghe,
                    "Oops...",
                    "Lỗi khi cập nhật"
                )
                t.printStackTrace()
            }

            override fun onResponse(
                call: Call<UpdateSelectedJobResponse>,
                response: Response<UpdateSelectedJobResponse>
            ) {
                processed()
                if (response.isSuccessful) {
                    setResult(ManHinhDanhSachNghe.infoJobRequestCode, Intent())
                    runOnUiThread {
                        val param1 = mhcctsnEdtParam1.text.toString()
                        val param2 = mhcctsnEdtParam2.text.toString()
                        Constants.selectedJob.jobId = Companion.jobId
                        Constants.selectedJob.jobInfoArray =
                            listOf(param1.toFloat(), param2.toFloat())
                        Constants.selectedJob.jobInfo =
                            GsonBuilder().create().toJson(Constants.selectedJob.jobInfoArray)
                        Constants.updateSelectedJob()
                    }
                    actRes = Activity.RESULT_OK
                    onBackPressed()
                    return
                }
                // Khi không thành công
                SimpleNotify.error(
                    this@ManHinhCungCapThongSoNghe,
                    "CHƯA ĐƯỢC",
                    "Đã xảy ra lỗi"
                )
            }
        })
    }

    private fun updateSelectedJob() {
        APIUtils.mAPIServices?.updateSelectedJobAPI(Constants.selectedJob)?.enqueue(object :
            Callback<UpdateSelectedJobResponse> {
            override fun onFailure(call: Call<UpdateSelectedJobResponse>, t: Throwable) {
                processed()
                SimpleNotify.error(
                    this@ManHinhCungCapThongSoNghe,
                    "Oops...",
                    "Lỗi khi cập nhật"
                )
            }

            override fun onResponse(
                call: Call<UpdateSelectedJobResponse>,
                response: Response<UpdateSelectedJobResponse>
            ) {
                if (response.isSuccessful) {
                    val captainSelectedJob = response.body()?.captainSelectedJob
                    if (captainSelectedJob != null) {
                        Constants.selectedJob.id =
                            captainSelectedJob.id ?: -1
                        Constants.selectedJob.captainId = captainSelectedJob.captainId ?: -1
                        Constants.updateSelectedJob()
                        // Xong
                        processed()
                        // Chuyển sang khởi tạo chuyến đi biển
                        val ktcdbIntent =
                            Intent(
                                this@ManHinhCungCapThongSoNghe,
                                ManHinhKhoiTaoChuyenDiBien::class.java
                            )
                        startActivity(ktcdbIntent)
                        // End màn hiện tại
                        finish()
                        return
                    }
                }
                // Khi không thành công
                SimpleNotify.error(
                    this@ManHinhCungCapThongSoNghe,
                    "CHƯA ĐƯỢC",
                    "Đã xảy ra lỗi"
                )
                processed()
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        if (!ManHinhDanhSachNghe.isEdit) {
            val manHinhDanhSachNgheIntent = Intent(this, ManHinhDanhSachNghe::class.java)
            startActivity(manHinhDanhSachNgheIntent)
            ToolbarBackButton(this).overrideAnimationOnEnterAndExitActivityReveret()
        } else {
            setResult(actRes, Intent())
        }
        finish()
        super.onBackPressed()
    }
}
