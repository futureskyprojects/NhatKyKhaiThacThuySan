package vn.vistark.nkktts.ui.thiet_lap

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cn.pedant.SweetAlert.SweetAlertDialog
import kotlinx.android.synthetic.main.man_hinh_thiet_lap.*
import vn.vistark.nkktts.R
import vn.vistark.nkktts.core.constants.Constants
import vn.vistark.nkktts.ui.doi_mat_khau.ManHinhDoiMatKhau
import vn.vistark.nkktts.ui.danh_sach_nghe.ManHinhDanhSachNghe
import vn.vistark.nkktts.ui.dang_nhap.ManHinhDangNhap
import vn.vistark.nkktts.ui.khoi_dong.ManHinhKhoiDong
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
                titleText = getString(R.string.dang_xuat).toUpperCase()
                contentText = getString(R.string.ban_co_chac_chan)
                setConfirmButton(getString(R.string.dong_y)) {
                    it.dismiss()
                    if (Constants.logOut()) {
                        val intent = Intent(this@ManHinhThietLap, ManHinhKhoiDong::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                    Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        SimpleNotify.error(
                            this@ManHinhThietLap,
                            getString(R.string.dang_xuat_loi).toUpperCase(),
                            getString(R.string.vui_long_thu_lai)
                        )
                    }
                }
                setCancelButton(getString(R.string.quay_ve)) {
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

    override fun onBackPressed() {
        SimpleNotify.onBackConfirm(this) {
            finish()
            super.onBackPressed()
        }
    }
}
