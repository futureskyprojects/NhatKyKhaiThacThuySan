package vn.vistark.nkktts.ui.me_danh_bat

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.man_hinh_me_danh_bat.*
import vn.vistark.nkktts.R
import vn.vistark.nkktts.ui.ket_thuc_chuyen_di_bien.ManHinhKetThucChuyenDiBien
import vn.vistark.nkktts.ui.danh_sach_loai.ManHinhDanhSachLoai
import vn.vistark.nkktts.utils.ToolbarBackButton

class ManHinhMeDanhBat : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.man_hinh_me_danh_bat)

        initEvents()
    }

    private fun initEvents() {
        mhmdbBtnThemMeMoi.setOnClickListener {
            val manHinhDanhSachLoaiItent = Intent(this, ManHinhDanhSachLoai::class.java)
            startActivity(manHinhDanhSachLoaiItent)
            finish()
        }

        mhmdbBtnKetThucChuyenDiBien.setOnClickListener {
            val manHinhKetThucChuyenDiBienItent =
                Intent(this, ManHinhKetThucChuyenDiBien::class.java)
            startActivity(manHinhKetThucChuyenDiBienItent)
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean { // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_chung, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return ToolbarBackButton(this).onOptionsItemSelected(item)
    }
}
