package vn.vistark.nkktts.ui.chon_cang

import SeaPorts
import SeaPortsReponse
import TheTripStorage
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import kotlinx.android.synthetic.main.man_hinh_chon_cang.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.vistark.nkktts.R
import vn.vistark.nkktts.core.api.APIUtils
import vn.vistark.nkktts.core.constants.Constants
import vn.vistark.nkktts.core.constants.OfflineDataStorage
import vn.vistark.nkktts.utils.SimpleNotify
import vn.vistark.nkktts.utils.ToolbarBackButton

class ManHinhChonCang : AppCompatActivity() {
    lateinit var pDialog: SweetAlertDialog
    lateinit var cangAdapter: CangAdapter
    var loaiCang = cangDi

    companion object {
        const val chonLoaiCang = "CHON_LOAI_CANG"
        const val cangDi = "CANG_DI"
        const val cangVe = "CANG_VE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.man_hinh_chon_cang)

        val extras = intent.extras
        if (extras?.getString(chonLoaiCang) != null) {
            loaiCang = extras.getString(chonLoaiCang)!!
        }
        initPreComponents()

        ToolbarBackButton(this).show()

        initEvents()
    }

    private fun initPreComponents() {
        // Progress dialog
        pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog.titleText = "Đang xử lý"
        pDialog.setCancelable(false)
    }

    private fun initDsCangBien(seaPorts: List<SeaPorts>) {
        mhccRvDanhSachCangBien.setHasFixedSize(true)
        mhccRvDanhSachCangBien.layoutManager = LinearLayoutManager(this)
        cangAdapter = CangAdapter(seaPorts)
        cangAdapter.onPortClick = {
            if (loaiCang == cangDi) {
                Constants.currentTrip = TheTripStorage()
                Constants.currentTrip.trip.departurePort = it.id
            } else {
                Constants.currentTrip.trip.destinationPort = it.id
            }
            if (Constants.updateCurrentTrip()) {
                setResult(Activity.RESULT_OK, Intent())
                finish()
            } else {
                SimpleNotify.error(this, "CHỌN CẢNG KHÔNG ĐƯỢC", "")
            }
        }
        mhccRvDanhSachCangBien.adapter = cangAdapter
    }

    fun processing() {
        if (!pDialog.isShowing) {
            pDialog.show()
        }
    }

    fun processed() {
        if (pDialog.isShowing) {
            pDialog.dismiss()
        }
    }

    private fun initEvents() {
        processing()
        val seaPortsReponse =
            OfflineDataStorage.get<SeaPortsReponse>(OfflineDataStorage.seaPorts)
        if (seaPortsReponse?.seaPorts != null) {
            initDsCangBien(seaPortsReponse.seaPorts)
        } else {
            SimpleNotify.error(
                this@ManHinhChonCang,
                "LẤY CẢNG KHÔNG ĐƯỢC",
                "Vui lòng thử lại"
            )
        }
        processed()
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
