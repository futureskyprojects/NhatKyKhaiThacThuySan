package vn.vistark.nkktts.ui.me_danh_bat

import Hauls
import Trip
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import kotlinx.android.synthetic.main.man_hinh_me_danh_bat.*
import vn.vistark.nkktts.R
import vn.vistark.nkktts.core.constants.Constants
import vn.vistark.nkktts.ui.chon_cang.ManHinhChonCang
import vn.vistark.nkktts.ui.ket_thuc_chuyen_di_bien.ManHinhKetThucChuyenDiBien
import vn.vistark.nkktts.ui.danh_sach_loai.ManHinhDanhSachLoai
import vn.vistark.nkktts.ui.khoi_tao_chuyen_di_bien.ManHinhKhoiTaoChuyenDiBien
import vn.vistark.nkktts.utils.SimpleNotify
import vn.vistark.nkktts.utils.ToolbarBackButton

class ManHinhMeDanhBat : AppCompatActivity() {
    val adapter = MeAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.man_hinh_me_danh_bat)

        initDsMe()

        initEvents()
    }

    private fun initDsMe() {
        mhmdbRvDsMeDanhBat.setHasFixedSize(true)
        mhmdbRvDsMeDanhBat.layoutManager = LinearLayoutManager(this)
        mhmdbRvDsMeDanhBat.adapter = adapter
        adapter.onHaulClick = {
            Hauls.currentHault = it
            val intent = Intent(this, ManHinhDanhSachLoai::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun initEvents() {
        mhmdbBtnThemMeMoi.setOnClickListener {
            Hauls.currentHault = Hauls() // Xóa hauls hiện tại để tạo mẻ mới
            val manHinhDanhSachLoaiItent = Intent(this, ManHinhDanhSachLoai::class.java)
            startActivity(manHinhDanhSachLoaiItent)
            finish()
        }

        mhmdbBtnKetThucChuyenDiBien.setOnClickListener {
            if (Constants.currentTrip.trip.hauls.isEmpty()) {
                SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE).apply {
                    titleText = "CHƯA CÓ MẺ NÀO"
                    contentText = "Vẫn thoát?"
                    setConfirmButton("Đồng ý") {
                        it.dismiss()
                        Constants.currentTrip.trip = Trip()
                        Constants.updateCurrentTrip()
                        startActivity(
                            Intent(
                                this@ManHinhMeDanhBat,
                                ManHinhKhoiTaoChuyenDiBien::class.java
                            )
                        )
                        finish()
                    }
                    setCancelButton("Hủy") {
                        it.dismiss()
                    }
                    show()
                }
            } else {
                val manHinhKetThucChuyenDiBienItent =
                    Intent(this, ManHinhKetThucChuyenDiBien::class.java)
                startActivity(manHinhKetThucChuyenDiBienItent)
                finish()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean { // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_trong_danh_sach_nghe, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return ToolbarBackButton(this).onOptionsItemSelected(item)
    }
}
