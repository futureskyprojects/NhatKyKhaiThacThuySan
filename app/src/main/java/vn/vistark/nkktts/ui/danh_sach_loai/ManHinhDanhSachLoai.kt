package vn.vistark.nkktts.ui.danh_sach_loai

import CatchedSpices
import Hauls
import Spices
import SpicesResponse
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.layout_item_san_luong_mac_dinh.*
import kotlinx.android.synthetic.main.man_hinh_danh_sach_loai.*
import vn.vistark.nkktts.R
import vn.vistark.nkktts.core.constants.Constants
import vn.vistark.nkktts.core.constants.OfflineDataStorage
import vn.vistark.nkktts.ui.me_danh_bat.ManHinhMeDanhBat
import vn.vistark.nkktts.ui.thong_tin_me_danh_bat.ManHinhThongTinMeDanhBat
import vn.vistark.nkktts.utils.*
import java.util.*
import kotlin.collections.ArrayList


class ManHinhDanhSachLoai : AppCompatActivity() {
    lateinit var manager: LocationManager
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    lateinit var pDialog: SweetAlertDialog
    lateinit var adapter: SpiceAdapter
    var pressedMillis = -1L
    var syncLocationManagerTimer: Timer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.man_hinh_danh_sach_loai)
        initPreComponents()
        initLocationServices()

//        ToolbarBackButton(this).show()
        initDanhSachLoai()
        initEvents()
        initIfNowIsReview()
    }

    private fun initIfNowIsReview() {
        if (Hauls.currentHault.timeCollectingNets.isNotEmpty()) {
            mhdslBtnKetThucMe.setBackgroundResource(R.drawable.btn_info)
            mhdslBtnKetThucMe.text = "Quay về"
            mhdslBtnKetThucMe.setOnClickListener {
                val manHinhMeDanhBatIntent =
                    Intent(this@ManHinhDanhSachLoai, ManHinhMeDanhBat::class.java)
                startActivity(manHinhMeDanhBatIntent)
                ToolbarBackButton(this@ManHinhDanhSachLoai).overrideAnimationOnEnterAndExitActivityReveret()
            }
        }
    }

    private fun initLocationServices() {
        manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        mFusedLocationClient =
            FusedLocationProviderClient(this@ManHinhDanhSachLoai);//LocationServices.getFusedLocationProviderClient(this)
        // Ngăn cản tắt GPS
        syncLocationManagerTimer = Timer()
        syncLocationManagerTimer?.schedule(object : TimerTask() {
            override fun run() {
                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    runOnUiThread {
                        if (!pDialog.isShowing || pDialog.titleText != "HÃY BẬT GPS") {
                            pDialog.titleText = "HÃY BẬT GPS"
                            pDialog.show()
                        }
                    }
                } else {
                    runOnUiThread {
                        if (pDialog.isShowing && pDialog.titleText == "HÃY BẬT GPS") {
                            pDialog.titleText = "Đang xử lý..."
                            pDialog.dismiss()
                        }
                    }
                    runOnUiThread {
                        SimpfyLocationUtils.requestNewLocationData(mFusedLocationClient)
                    }
                }
            }
        }, 1000, 5000)
        // Lấy vị trí
    }

    private fun showBottomSheetCapNhatSanLuong(spices: Spices) {
        mhdslLayoutNhapSanLuong.visibility = View.VISIBLE
//        mhdslBtnThemLoaiKhac.visibility = View.GONE;
        mhdslBtnKetThucMe.visibility = View.GONE
        // Xử lý khung nhìn
        if (spices.image != null && spices.image.isNotEmpty()) {
            val bm = Base64ToBitmap.process(spices.image)
            if (bm != null) {
                islmdHinhAnhLoai.setImageBitmap(bm)
            }
        }
        islmdTenLoai.text = spices.name
        islmdBtnCapNhatSanLuong.setOnClickListener {
            if (pressedMillis == -1L) {
                pressedMillis = System.currentTimeMillis()
                SimpfyLocationUtils.requestNewLocationData(mFusedLocationClient)
            } else if (System.currentTimeMillis() - pressedMillis > 5000) {
                SimpfyLocationUtils.getLastLocation(mFusedLocationClient)
            }
            if (SimpfyLocationUtils.mLastLocation != null) {
                pressedMillis = -1
                val input = islmdEdtSanLuong.text.toString().toFloatOrNull()
                if (input == null) {
                    SimpleNotify.error(this, "SẢN LƯỢNG SAI", "")
                } else {
                    var isExists = false
                    if (Hauls.currentHault.spices.isNotEmpty()) {
                        for (i in Hauls.currentHault.spices.indices) {
                            if (Hauls.currentHault.spices[i].id == spices.id) {
                                Hauls.currentHault.spices[i].name = spices.name ?: "<Không rõ>"
                                Hauls.currentHault.spices[i].weight = input
                                isExists = true
                            }
                        }
                    }

                    if (!isExists) {
                        val catchedSpices = CatchedSpices()
                        catchedSpices.id = spices.id
                        catchedSpices.name = spices.name ?: "<Không rõ>"
                        catchedSpices.weight = input

                        val temp = ArrayList(Hauls.currentHault.spices)
                        temp.add(catchedSpices)
                        Hauls.currentHault.spices = temp.toList()
                    }
                    // Nếu Haul này chưa được khởi tạo trước đó, tiến hành khởi tạo mới
                    if (Hauls.currentHault.orderNumber < 0) {
                        var orderNumber = 1
                        if (Constants.currentTrip.trip.hauls.isNotEmpty()) {
                            orderNumber = Constants.currentTrip.trip.hauls.last().orderNumber + 1
                        }
                        Hauls.currentHault.orderNumber = orderNumber
                        Hauls.currentHault.timeDropNets = DateTimeUtils.getStringCurrentYMDHMS()
                        Hauls.currentHault.latDrop =
                            SimpfyLocationUtils.mLastLocation!!.latitude.toString()
                        Hauls.currentHault.lngDrop =
                            SimpfyLocationUtils.mLastLocation!!.longitude.toString()
                        Hauls.updateHault()
                    }
                    // Lưu vào bộ nhớ
                    Constants.updateCurrentTrip()
                    // Ẩn đi
                    hideBottomSheetCapNhatSanLuong()
                    // Thông báo thay đổi
                    adapter.notifyDataSetChanged()
                }
            } else {
                SimpleNotify.warning(this, "ĐANG LẤY VỊ TRÍ", "Thử lại sau 1 giây")
            }
        }
        // Load dữ liệu cũ nếu có
        islmdEdtSanLuong.setText("0")
        if (Hauls.currentHault.spices.isNotEmpty()) {
            for (spice in Hauls.currentHault.spices) {
                if (spice.id == spices.id) {
                    islmdTenLoai.text = spice.name
                    islmdEdtSanLuong.setText(spice.weight.toString())
                }
            }
        }
    }

    fun hideBottomSheetCapNhatSanLuong() {
        mhdslLayoutNhapSanLuong.visibility = View.GONE
//        mhdslBtnThemLoaiKhac.visibility = View.VISIBLE;
        mhdslBtnKetThucMe.visibility = View.VISIBLE;
    }

    private fun initEvents() {
        // Loài trong danh sách - Hiển thị bottomshet và các event liên quan
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
            if (Hauls.currentHault.spices.isEmpty()) {
                startActivity(Intent(this@ManHinhDanhSachLoai, ManHinhMeDanhBat::class.java))
                return@setOnClickListener
            }
            if (pressedMillis == -1L) {
                pressedMillis = System.currentTimeMillis()
                SimpfyLocationUtils.requestNewLocationData(mFusedLocationClient)
            } else if (System.currentTimeMillis() - pressedMillis > 5000) {
                SimpfyLocationUtils.getLastLocation(mFusedLocationClient)
            }
            if (SimpfyLocationUtils.mLastLocation != null) {
                pressedMillis = -1
                var isUpdated = false
                if (Hauls.currentHault.spices.isNotEmpty()) {
                    for (i in Hauls.currentHault.spices.indices) {
                        Hauls.currentHault.latCollecting =
                            SimpfyLocationUtils.mLastLocation!!.latitude.toString()
                        Hauls.currentHault.lngCollecting =
                            SimpfyLocationUtils.mLastLocation!!.longitude.toString()
                        Hauls.updateHault()
                        isUpdated = true
                    }
                }

                if (isUpdated && Constants.updateCurrentTrip()) {
                    stopTimer()
                    val thongTinMeDanhBatIntent = Intent(this, ManHinhThongTinMeDanhBat::class.java)
                    startActivity(thongTinMeDanhBatIntent)
                    finish()
                } else {
                    SimpleNotify.error(this, "KẾT THÚC LỖI", "Vui lòng thử lại")
                }
            } else {
                SimpleNotify.warning(
                    this,
                    "ĐANG LẤY VỊ TRÍ",
                    "Thử lại sau 5 giây hoặc lâu hơn nếu GPS kém."
                )
            }
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
            pDialog.dismiss()
        }
    }

    fun initDanhSachLoai() {
        processing()
        val seaPortsReponse =
            OfflineDataStorage.get<SpicesResponse>(OfflineDataStorage.spices)
        if (seaPortsReponse?.spices != null) {
            initDsLoai(seaPortsReponse.spices)
        } else {
            SimpleNotify.error(
                this,
                "LẤY LOÀI LỖI",
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
        adapter.onSpiceClick = {
            // Nếu không phải xem lại
            if (Hauls.currentHault.timeCollectingNets.isEmpty())
                showBottomSheetCapNhatSanLuong(it)
        }
        mhdslRvDsLoai.adapter = adapter
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1234 -> {
                if (grantResults.size >= 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    permissionRequest()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun permissionRequest() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            1234
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean { // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_trong_danh_sach_nghe, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return ToolbarBackButton(this).onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTimer()
    }

    fun stopTimer() {
        syncLocationManagerTimer?.cancel()
        syncLocationManagerTimer = null
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
