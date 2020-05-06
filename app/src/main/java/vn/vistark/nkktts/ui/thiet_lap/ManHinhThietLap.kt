package vn.vistark.nkktts.ui.thiet_lap

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import cn.pedant.SweetAlert.SweetAlertDialog
import kotlinx.android.synthetic.main.man_hinh_thiet_lap.*
import vn.vistark.nkktts.R
import vn.vistark.nkktts.core.constants.Constants
import vn.vistark.nkktts.ui.change_password.ManHinhDoiMatKhau
import vn.vistark.nkktts.ui.danh_sach_nghe.ManHinhDanhSachNghe
import vn.vistark.nkktts.ui.khai_bao_thong_tin_ho_so.ManHinhKhaiBaoThongTinHoSo
import vn.vistark.nkktts.ui.man_hinh_dang_nhap.ManHinhDangNhap
import vn.vistark.nkktts.ui.sua_ho_so.ManHinhSuaHoSo
import vn.vistark.nkktts.utils.SimpleNotify
import vn.vistark.nkktts.utils.ToolbarBackButton

class ManHinhThietLap : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.man_hinh_thiet_lap)

        ToolbarBackButton(this).show()

        initData()

        initEvents()
    }

    private fun initData() {
        mhtlTvTenThuyenTruong.text = Constants.userInfo.captain
    }

    private fun initEvents() {
        rlBtnSuaThongTin.setOnClickListener {
            val intent = Intent(this, ManHinhSuaHoSo::class.java)
            startActivity(intent)
        }

        rlBtnDoiNghe.setOnClickListener {
            val intent = Intent(this, ManHinhDanhSachNghe::class.java)
            ManHinhDanhSachNghe.isEdit = true
            startActivity(intent)
        }

        rlBtnDoiMatKhau.setOnClickListener {
            val intent = Intent(this, ManHinhDoiMatKhau::class.java)
            ManHinhDoiMatKhau.isChangePassword = true
            startActivity(intent)
        }

        mhtlBtnDangXuat.setOnClickListener {
            SweetAlertDialog(this).apply {
                titleText = "ĐĂNG XUẤT"
                contentText = "Bạn chắc chắn?"
                setConfirmButton("Đồng ý") {
                    if (Constants.logOut()) {
                        startActivity(Intent(this@ManHinhThietLap, ManHinhDangNhap::class.java))
                        finish()
                    } else {
                        SimpleNotify.error(
                            this@ManHinhThietLap,
                            "ĐĂNG XUẤT LỖI",
                            "Vui lòng thử lại"
                        )
                    }
                }
                setCancelButton("Quay lại") {
                    it.dismissWithAnimation()
                }
                show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
