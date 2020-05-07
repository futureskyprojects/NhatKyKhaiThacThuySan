package vn.vistark.nkktts.ui.thong_tin_me_danh_bat

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.man_hinh_thong_tin_me_danh_bat.*
import vn.vistark.nkktts.R
import vn.vistark.nkktts.core.constants.Constants
import vn.vistark.nkktts.ui.me_danh_bat.ManHinhMeDanhBat
import vn.vistark.nkktts.ui.danh_sach_loai.ManHinhDanhSachLoai
import vn.vistark.nkktts.utils.DateTimeUtils
import vn.vistark.nkktts.utils.SimpfyLocationUtils
import vn.vistark.nkktts.utils.SimpleNotify
import vn.vistark.nkktts.utils.ToolbarBackButton

class ManHinhThongTinMeDanhBat : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.man_hinh_thong_tin_me_danh_bat)

        ToolbarBackButton(this).show()

        initDataShow()

        intEvents()
    }

    @SuppressLint("SetTextI18n")
    private fun initDataShow() {
        if (Hauls.currentHault.orderNumber < 0) {
            onBackPressed()
        } else {
            var calSum = 0F
            for (spice in Hauls.currentHault.spices) {
                calSum += spice.weight
            }
            mhttmdbTvThoiGianTha.text = Hauls.currentHault.timeDropNets
            mhttmdbTvViTha.text = "${Hauls.currentHault.latDrop},${Hauls.currentHault.lngDrop}"
            mhttmdbTvThoiGianThu.text = DateTimeUtils.getStringCurrentYMDHMS()
            mhttmdbTvViTriThu.text =
                "${Hauls.currentHault.latCollecting},${Hauls.currentHault.lngCollecting}"
            mhttmdbTvTongSanLuong.text = (calSum / 1000).toString()
        }
    }

    private fun intEvents() {
        mhttmdbBtnLuu.setOnClickListener {
            var isUpdated = false
            if (Hauls.currentHault.spices.isNotEmpty()) {
                for (i in Hauls.currentHault.spices.indices) {
                    Hauls.currentHault.timeCollectingNets =
                        mhttmdbTvThoiGianThu.text.toString()
                    Hauls.updateHault()
                    isUpdated = true
                }
            }
            if (isUpdated && Constants.updateCurrentTrip()) {
                val manHinhMeDanhBatIntent = Intent(this, ManHinhMeDanhBat::class.java)
                startActivity(manHinhMeDanhBatIntent)
                finish()
            } else {
                SimpleNotify.warning(
                    this,
                    getString(R.string.luu_khong_duoc),
                    getString(R.string.vui_long_thu_lai)
                )
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        SimpleNotify.onBackConfirm(this) {
            val manHinhDanhSachLoaiIntent = Intent(this, ManHinhDanhSachLoai::class.java)
            startActivity(manHinhDanhSachLoaiIntent)
            finish()
            super.onBackPressed()
        }
    }
}
