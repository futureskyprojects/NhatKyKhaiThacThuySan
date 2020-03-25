package vn.vistark.nkktts.ui.ket_thuc_chuyen_di_bien

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.man_hinh_ket_thuc_chuyen_di_bien.*
import vn.vistark.nkktts.R
import vn.vistark.nkktts.ui.chon_cang.ManHinhChonCang
import vn.vistark.nkktts.ui.danh_sach_nghe.ManHinhDanhSachNghe
import vn.vistark.nkktts.ui.danh_sach_loai.ManHinhDanhSachLoai
import vn.vistark.nkktts.utils.ToolbarBackButton

class ManHinhKetThucChuyenDiBien : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.man_hinh_ket_thuc_chuyen_di_bien)
        ToolbarBackButton(this).show()

        initEvents()
    }
    private fun initEvents() {
        mhktcdbLnChonCang.setOnClickListener {
            val intent = Intent(this, ManHinhChonCang::class.java)
            intent.putExtra(ManHinhChonCang.chonLoaiCang, ManHinhChonCang.cangVe)
            startActivity(intent)
        }

        mhktcdbBtnNopNhatKy.setOnClickListener {
            val manHinhDanhSachNghe = Intent(this, ManHinhDanhSachNghe::class.java)
            startActivity(manHinhDanhSachNghe)
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        val manHinhDanhSachLoai = Intent(this, ManHinhDanhSachLoai::class.java)
        startActivity(manHinhDanhSachLoai)
        ToolbarBackButton(this).overrideAnimationOnEnterAndExitActivityReveret()
        finish()
        super.onBackPressed()
    }

}
