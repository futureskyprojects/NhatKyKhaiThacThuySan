package vn.vistark.nkktts.ui.thiet_lap

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.man_hinh_thiet_lap.*
import vn.vistark.nkktts.R
import vn.vistark.nkktts.ui.khai_bao_thong_tin_ho_so.ManHinhKhaiBaoThongTinHoSo
import vn.vistark.nkktts.utils.ToolbarBackButton

class ManHinhThietLap : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.man_hinh_thiet_lap)

        ToolbarBackButton(this).show()

        initEvents()
    }

    private fun initEvents() {
        rlBtnSuaThongTin.setOnClickListener {
            val manHinhKhaiBaoThongTinHoSo = Intent(this, ManHinhKhaiBaoThongTinHoSo::class.java)
            startActivity(manHinhKhaiBaoThongTinHoSo)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
