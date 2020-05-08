package vn.vistark.nkktts.ui.lich_su_chuyen_di

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.SyncStateContract
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.man_hinh_lich_su_chuyen_di.*
import vn.vistark.nkktts.R
import vn.vistark.nkktts.core.constants.Constants
import vn.vistark.nkktts.core.db.TripWaitForSync
import vn.vistark.nkktts.utils.ToolbarBackButton

class ManHinhLichSuChuyenDi : AppCompatActivity() {
    lateinit var waitForSync: TripWaitForSync
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.man_hinh_lich_su_chuyen_di)

        supportActionBar?.title = getString(R.string.lich_su_chuyen_di)

        // Dành cho phần chưa đồng bộ
        waitForSync = TripWaitForSync(this)
        val wfs = waitForSync.getAll().filter { it.trip.trip.captainId == Constants.userId.toInt() }
            .toTypedArray()
        if (wfs.any { !it.trip.trip.isSynced }) {
            tvS1.text = getString(R.string.con_d_chuyen, wfs.filter { !it.trip.trip.isSynced }.size)
        } else {
            tvS1.text = getString(R.string.da_dong_bo_tat_ca)
        }
        rvNotSynced.setHasFixedSize(true)
        rvNotSynced.isNestedScrollingEnabled = false
        rvNotSynced.layoutManager = LinearLayoutManager(this)
        rvNotSynced.adapter = LichSuChuyenDiChuaDongBoAdapter(wfs)

        // Dành cho dữ liệu lấy được từ server
        rvSyncedToServer.setHasFixedSize(true)
        rvSyncedToServer.isNestedScrollingEnabled = false
        rvSyncedToServer.layoutManager = LinearLayoutManager(this)
        rvSyncedToServer.adapter = LichSuChuyenDiAdapter()

        // Hiển thị nút trở về
        ToolbarBackButton(this).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
