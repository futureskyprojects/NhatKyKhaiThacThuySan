package vn.vistark.nkktts.ui.man_hinh_khoi_dong

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import vn.vistark.nkktts.R
import vn.vistark.nkktts.core.constants.Constants
import vn.vistark.nkktts.ui.danh_sach_nghe.ManHinhDanhSachNghe
import vn.vistark.nkktts.ui.khoi_tao_chuyen_di_bien.ManHinhKhoiTaoChuyenDiBien
import vn.vistark.nkktts.ui.man_hinh_dang_nhap.ManHinhDangNhap
import vn.vistark.nkktts.utils.DataInitialize
import vn.vistark.nkktts.utils.SimpleNotify
import java.util.*

class ManHinhKhoiDong : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.man_hinh_khoi_dong)

        initPre()
        DataInitialize() // Fetch data from internet

        Timer().schedule(object : TimerTask() {
            override fun run() {
                if (DataInitialize.isFinished()) {
                    if (!DataInitialize.isInitSeaPortSuccess) {
                        DataInitialize() // Lấy lại lần nữa
                        SimpleNotify.error(this@ManHinhKhoiDong, "Oops...", "Lấy cảng lỗi")
                    } else if (!DataInitialize.isInitSpiceSuccess) {
                        DataInitialize() // Lấy lại lần nữa
                        SimpleNotify.error(this@ManHinhKhoiDong, "Oops...", "Lấy loài lỗi")
                    } else if (!DataInitialize.isInitJobSuccess) {
                        DataInitialize() // Lấy lại lần nữa
                        SimpleNotify.error(this@ManHinhKhoiDong, "Oops...", "Lấy nghề lỗi")
                    } else {
                        this.cancel()
                        chuyenQuaManHinhDangNhap()
                    }
                }
            }
        }, 300, 1000)
    }

    @SuppressLint("DefaultLocale")
    private fun initPre() {
        Constants.sharedPreferences =
            getSharedPreferences(application.packageName.toUpperCase(), Context.MODE_PRIVATE)
    }

    private fun chuyenQuaManHinhDangNhap() {
        if (Constants.sharedPreferences == null) {
            SimpleNotify.error(this, "LỖI DỮ LIỆU", "Khởi tạo trình đọc dữ liệu không được")
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    chuyenQuaManHinhDangNhap()
                    finish()
                }
            }, 500)
        } else {
            Constants.readAllSavedData()
            // Kiểm tra xem đã đăng nhập chưa
            if (Constants.isLoggedIn()) {
                if (Constants.isSelectedJob()) {
                    // Nếu đã chọn nghề
                    val ktcdbIntent =
                        Intent(
                            this,
                            ManHinhKhoiTaoChuyenDiBien::class.java
                        )
                    startActivity(ktcdbIntent)
                    finish()
                } else {
                    // Vào màn hình chọn nghề khi chưa chọn
                    startActivity(Intent(this, ManHinhDanhSachNghe::class.java))
                    finish()
                }
            } else {
                // Vào màn hình đăng nhập
                startActivity(Intent(this, ManHinhDangNhap::class.java))
                finish()
            }
        }
    }

}
