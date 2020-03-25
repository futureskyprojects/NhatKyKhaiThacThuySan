package vn.vistark.nkktts.ui.danh_sach_loai

import Spices
import SpicesResponse
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import kotlinx.android.synthetic.main.item_loai.*
import kotlinx.android.synthetic.main.layout_item_san_luong_mac_dinh.*
import kotlinx.android.synthetic.main.man_hinh_danh_sach_loai.*
import vn.vistark.nkktts.R
import vn.vistark.nkktts.core.constants.Constants
import vn.vistark.nkktts.core.constants.OfflineDataStorage
import vn.vistark.nkktts.ui.me_danh_bat.ManHinhMeDanhBat
import vn.vistark.nkktts.ui.thong_tin_me_danh_bat.ManHinhThongTinMeDanhBat
import vn.vistark.nkktts.utils.SimpleNotify
import vn.vistark.nkktts.utils.ToolbarBackButton

class ManHinhDanhSachLoai : AppCompatActivity() {
    lateinit var pDialog: SweetAlertDialog
    lateinit var adapter: SpiceAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.man_hinh_danh_sach_loai)
        initPreComponents()

//        ToolbarBackButton(this).show()
        initDanhSachLoai()
        initEvents()
    }

    fun showBottomSheetCapNhatSanLuong() {
        mhdslLayoutNhapSanLuong.visibility = View.VISIBLE
//        mhdslBtnThemLoaiKhac.visibility = View.GONE;
        mhdslBtnKetThucMe.visibility = View.GONE;
    }

    fun hideBottomSheetCapNhatSanLuong() {
        mhdslLayoutNhapSanLuong.visibility = View.GONE
//        mhdslBtnThemLoaiKhac.visibility = View.VISIBLE;
        mhdslBtnKetThucMe.visibility = View.VISIBLE;
    }

    private fun initEvents() {
        // Loài trong danh sách - Hiển thị bottomshet và các event liên quan
//        ilLnLoai.setOnClickListener {
//            showBottomSheetCapNhatSanLuong()
//        }
        islmdBtnCapNhatSanLuong.setOnClickListener {
            hideBottomSheetCapNhatSanLuong()
        }

        islmdIvBtnNutDong.setOnClickListener {
            hideBottomSheetCapNhatSanLuong()
        }
        // Loài khác - Hiển thị bottomshet và các event liên quan
        mhdslBtnThemLoaiKhac.setOnClickListener {
            // Tạm thời bỏ qua
        }
        // Nút kết thúc
        mhdslBtnKetThucMe.setOnClickListener {
            val thongTinMeDanhBatIntent = Intent(this, ManHinhThongTinMeDanhBat::class.java)
            startActivity(thongTinMeDanhBatIntent)
            finish()
        }
    }

    private fun initPreComponents() {
        // Progress dialog
        pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog.titleText = "Đang xử lý"
        pDialog.setCancelable(false)
    }

    fun processing() {
        if (!pDialog.isShowing) {
            pDialog.show()
        }
    }

    fun processed() {
        if (pDialog.isShowing) {
            pDialog.hide()
        }
    }

    fun initDanhSachLoai() {
        processing()
        val seaPortsReponse =
            OfflineDataStorage.get<SpicesResponse>(OfflineDataStorage.spices)
        if (seaPortsReponse != null) {
            initDsLoai(seaPortsReponse.spices)
        } else {
            SimpleNotify.error(
                this,
                "LẤY CẢNG KHÔNG ĐƯỢC",
                "Vui lòng thử lại"
            )
        }
        processed()
    }

    private fun initDsLoai(spices: List<Spices>) {
        mhdslRvDsLoai.setHasFixedSize(true)
        mhdslRvDsLoai.layoutManager = GridLayoutManager(this, 2)
        val spicesInJobs = ArrayList<Spices>()
        // Lọc
        for (spice in spices) {
            if (spice.typeJob == Constants.selectedJob.jobId) {
                spicesInJobs.add(spice)
            }
        }
        adapter = SpiceAdapter(spicesInJobs)
        mhdslRvDsLoai.adapter = adapter
    }

//    override fun onSupportNavigateUp(): Boolean {
//        onBackPressed()
//        return true
//    }

//    override fun onBackPressed() {
//        val alertDialog = AlertDialog.Builder(this).apply {
//            setTitle("XÁC NHẬN QUAY LẠI")
//            setMessage("Bạn sẽ mất tất cả dữ liệu khi chưa kết thúc mẻ. Vẫn quay lại?")
//            setPositiveButton("Đồng ý") { d, w ->
//                val manHinhMeDanhBatIntent =
//                    Intent(this@ManHinhDanhSachLoai, ManHinhMeDanhBat::class.java)
//                startActivity(manHinhMeDanhBatIntent)
//                ToolbarBackButton(this@ManHinhDanhSachLoai).overrideAnimationOnEnterAndExitActivityReveret()
//                super.onBackPressed()
//            }
//            setNegativeButton("Không") { d, w ->
//                d.dismiss()
//            }
//        }
//        alertDialog.show()
//    }
}
