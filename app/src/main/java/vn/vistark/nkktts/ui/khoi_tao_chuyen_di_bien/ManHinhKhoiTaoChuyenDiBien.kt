package vn.vistark.nkktts.ui.khoi_tao_chuyen_di_bien

import SeaPortsReponse
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.man_hinh_khoi_tao_chuyen_di_bien.*
import vn.vistark.nkktts.R
import vn.vistark.nkktts.core.constants.Constants
import vn.vistark.nkktts.core.constants.OfflineDataStorage
import vn.vistark.nkktts.ui.chon_cang.ManHinhChonCang
import vn.vistark.nkktts.ui.me_danh_bat.ManHinhMeDanhBat
import vn.vistark.nkktts.utils.DateTimeUtils
import vn.vistark.nkktts.utils.SimpleNotify
import vn.vistark.nkktts.utils.ToolbarBackButton

class ManHinhKhoiTaoChuyenDiBien : AppCompatActivity() {
    companion object {
        const val requestSeaPortCode = 1002
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.man_hinh_khoi_tao_chuyen_di_bien)
//        ToolbarBackButton(this).show()
        showPreviousSelectedSeaPort()

        initEvents()
        supportActionBar?.title = getString(R.string.khoi_tao_chuyen_di_bien)
    }

    private fun showPreviousSelectedSeaPort() {
        if (Constants.currentTrip.trip.departurePort >= 0) {
            val seaPortsReponse =
                OfflineDataStorage.get<SeaPortsReponse>(OfflineDataStorage.seaPorts)
            if (seaPortsReponse?.seaPorts != null) {
                mhktcdbIvIcon.setImageResource(R.drawable.ic_pin)
                for (seaPort in seaPortsReponse.seaPorts) {
                    if (seaPort.id == Constants.currentTrip.trip.departurePort) {
                        mhktcdbTvTenCang.text = seaPort.name
                        return
                    }
                }
            }
        }
    }

    private fun initEvents() {
        mhktcdbLnChonCang.setOnClickListener {
            val intent = Intent(this, ManHinhChonCang::class.java)
            intent.putExtra(ManHinhChonCang.chonLoaiCang, ManHinhChonCang.cangDi)
            startActivityForResult(intent, requestSeaPortCode)
        }

        ktcdbBtnBatDau.setOnClickListener {
            if (Constants.currentTrip.trip.departurePort >= 0) {
                Constants.currentTrip.trip.captainId = Constants.userId.toInt()
                Constants.currentTrip.trip.departureTime = DateTimeUtils.getStringCurrentYMD()
                if (Constants.updateCurrentTrip()) {
                    val manHinhMeDanhBatIntent = Intent(this, ManHinhMeDanhBat::class.java)
                    startActivity(manHinhMeDanhBatIntent)
                    finish()
                } else {
                    SimpleNotify.error(
                        this,
                        getString(R.string.vui_long_thu_lai),
                        getString(R.string.loi).toUpperCase()
                    )
                }
            } else {
                SimpleNotify.warning(
                    this,
                    getString(R.string.vui_long_chon_cang),
                    getString(R.string.chua_chon_cang).toUpperCase()
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == requestSeaPortCode) {
            if (resultCode == Activity.RESULT_OK) {
                showPreviousSelectedSeaPort()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean { // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_trong_danh_sach_nghe, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return ToolbarBackButton(this).onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        SimpleNotify.onBackConfirm(this) {
            finish()
            super.onBackPressed()
        }
    }

//    override fun onSupportNavigateUp(): Boolean {
//        onBackPressed()
//        return true
//    }
//
//    override fun onBackPressed() {
//        val manHinhNgheCauIntent = Intent(this, ManHinhCungCapThongSoNghe::class.java)
//        startActivity(manHinhNgheCauIntent)
//        ToolbarBackButton(this).overrideAnimationOnEnterAndExitActivityReveret()
//        finish()
//        super.onBackPressed()
//    }
}
