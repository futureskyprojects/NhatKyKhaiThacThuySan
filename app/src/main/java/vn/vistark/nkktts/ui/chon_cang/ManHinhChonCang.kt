package vn.vistark.nkktts.ui.chon_cang

import SeaPorts
import SeaPortsReponse
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
            pDialog.hide()
        }
    }

    private fun initEvents() {
        processing()
        val seaPortsReponse =
            OfflineDataStorage.get<SeaPortsReponse>(OfflineDataStorage.seaPorts)
        if (seaPortsReponse != null) {
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
}
