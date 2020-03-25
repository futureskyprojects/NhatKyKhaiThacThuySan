package vn.vistark.nkktts.ui.thong_tin_me_danh_bat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.man_hinh_thong_tin_me_danh_bat.*
import vn.vistark.nkktts.R
import vn.vistark.nkktts.ui.me_danh_bat.ManHinhMeDanhBat
import vn.vistark.nkktts.ui.danh_sach_loai.ManHinhDanhSachLoai
import vn.vistark.nkktts.utils.ToolbarBackButton

class ManHinhThongTinMeDanhBat : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.man_hinh_thong_tin_me_danh_bat)

        ToolbarBackButton(this).show()

        intEvents()
    }

    private fun intEvents() {
        mhttmdbBtnLuu.setOnClickListener {
            val manHinhMeDanhBatIntent = Intent(this, ManHinhMeDanhBat::class.java)
            startActivity(manHinhMeDanhBatIntent)
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        val manHinhDanhSachLoaiIntent = Intent(this, ManHinhDanhSachLoai::class.java)
        startActivity(manHinhDanhSachLoaiIntent)
        finish()
        super.onBackPressed()
    }
}
