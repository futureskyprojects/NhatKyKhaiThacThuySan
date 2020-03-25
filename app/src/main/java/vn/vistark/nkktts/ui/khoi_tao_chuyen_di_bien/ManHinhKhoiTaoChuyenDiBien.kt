package vn.vistark.nkktts.ui.khoi_tao_chuyen_di_bien

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.man_hinh_khoi_tao_chuyen_di_bien.*
import vn.vistark.nkktts.R
import vn.vistark.nkktts.ui.cung_cap_thong_so_nghe.ManHinhCungCapThongSoNghe
import vn.vistark.nkktts.ui.chon_cang.ManHinhChonCang
import vn.vistark.nkktts.ui.me_danh_bat.ManHinhMeDanhBat
import vn.vistark.nkktts.utils.ToolbarBackButton

class ManHinhKhoiTaoChuyenDiBien : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.man_hinh_khoi_tao_chuyen_di_bien)

//        ToolbarBackButton(this).show()

        initEvents()
    }

    private fun initEvents() {
        mhktcdbLnChonCang.setOnClickListener {
            val intent = Intent(this, ManHinhChonCang::class.java)
            startActivity(intent)
        }

        ktcdbBtnBatDau.setOnClickListener {
            val manHinhMeDanhBatIntent = Intent(this, ManHinhMeDanhBat::class.java)
            intent.putExtra(ManHinhChonCang.chonLoaiCang, ManHinhChonCang.cangDi)
            startActivity(manHinhMeDanhBatIntent)
            finish()
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
