package vn.vistark.nkktts.ui.danh_sach_nghe

import GetJobsResponse
import Jobs
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import kotlinx.android.synthetic.main.man_hinh_danh_sach_nghe.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.vistark.nkktts.R
import vn.vistark.nkktts.core.api.APIUtils
import vn.vistark.nkktts.core.constants.Constants
import vn.vistark.nkktts.core.constants.OfflineDataStorage
import vn.vistark.nkktts.ui.cung_cap_thong_so_nghe.ManHinhCungCapThongSoNghe
import vn.vistark.nkktts.utils.Base64ToBitmap
import vn.vistark.nkktts.utils.SimpleNotify
import vn.vistark.nkktts.utils.ToolbarBackButton


class ManHinhDanhSachNghe : AppCompatActivity() {
    lateinit var pDialog: SweetAlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.man_hinh_danh_sach_nghe)

        initPreComponents()
        initJobsDataFromAPI()
    }

    fun processing() {
        if (!pDialog.isShowing)
            pDialog.show()
    }

    fun processed() {
        if (pDialog.isShowing)
            pDialog.hide()
    }

    private fun initPreComponents() {
        // Progress dialog
        pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog.titleText = "Đang xử lý"
        pDialog.setCancelable(false)
    }

    fun job1(jobs: Jobs) {
        initJobSelectionEvent(dsnLnBtnNgheCau, jobs.id)
        dsnLnBtnNgheCau.isEnabled = (jobs.status == 1)
        if (jobs.image.isNotEmpty()) {
            val bm = Base64ToBitmap.process(jobs.image)
            dsnIvNgheCau.setImageBitmap(bm)
        }
        dsnTvNgheCau.text = jobs.jobName
    }

    fun job2(jobs: Jobs) {
        initJobSelectionEvent(dsnLnBtnNgheLuoiVay, jobs.id)
        dsnLnBtnNgheLuoiVay.isEnabled = (jobs.status == 1)
        if (jobs.image.isNotEmpty()) {
            val bm = Base64ToBitmap.process(jobs.image)
            dsnIvNgheLuoiVay.setImageBitmap(bm)
        }
        dsnTvNgheLuoiVay.text = jobs.jobName
    }

    fun job3(jobs: Jobs) {
        initJobSelectionEvent(dsnLnBtnNgheLuoiChup, jobs.id)
        dsnLnBtnNgheLuoiChup.isEnabled = (jobs.status == 1)
        if (jobs.image.isNotEmpty()) {
            val bm = Base64ToBitmap.process(jobs.image)
            dsnIvNgheLuoiChup.setImageBitmap(bm)
        }
        dsnTvNgheLuoiChup.text = jobs.jobName
    }

    fun job4(jobs: Jobs) {
        initJobSelectionEvent(dsnLnBtnNgheLuoiKeo, jobs.id)
        dsnLnBtnNgheLuoiKeo.isEnabled = (jobs.status == 1)
        if (jobs.image.isNotEmpty()) {
            val bm = Base64ToBitmap.process(jobs.image)
            dsnIvNgheLuoiKeo.setImageBitmap(bm)
        }
        dsnTvNgheLuoiKeo.text = jobs.jobName
    }

    fun job5(jobs: Jobs) {
        dsnLnBtnNgheKhac.isEnabled = (jobs.status == 1)
        if (jobs.image.isNotEmpty()) {
            val bm = Base64ToBitmap.process(jobs.image)
            dsnIvNgheKhac.setImageBitmap(bm)
        }
        dsnTvNgheKhac.text = jobs.jobName
    }

    fun jobsProcessing(getJobsResponse: GetJobsResponse) {
        for (job in getJobsResponse.jobs) {
            when (job.id) {
                1 -> job1(job)
                2 -> job2(job)
                3 -> job3(job)
                4 -> job4(job)
                else -> job5(job)
            }
        }
    }

    private fun initJobsDataFromAPI() {
        processing()
        val getJobsResponse =
            OfflineDataStorage.get<GetJobsResponse>(OfflineDataStorage.jobs)
        if (getJobsResponse != null) {
            jobsProcessing(getJobsResponse)
        } else {
            SimpleNotify.error(
                this@ManHinhDanhSachNghe,
                "LẤY NGHỀ THẤT BẠI",
                "Hãy thử thoát ra và vào lại",
                false
            )
        }
        processed()
    }

    private fun initJobSelectionEvent(v: View, id: Int) {
        v.setOnClickListener {
            Constants.selectedJob.jobId = id

            val intent = Intent(this, ManHinhCungCapThongSoNghe::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean { // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_trong_danh_sach_nghe, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return ToolbarBackButton(this).onOptionsItemSelected(item)
    }

}
