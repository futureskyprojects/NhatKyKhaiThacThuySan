package vn.vistark.nkktts.ui.danh_sach_loai

import CatchedSpices
import Hauls
import Spices
import SpicesResponse
import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.GridLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.layout_item_san_luong_mac_dinh.*
import kotlinx.android.synthetic.main.man_hinh_danh_sach_loai.*
import vn.vistark.nkktts.R
import vn.vistark.nkktts.core.constants.Constants
import vn.vistark.nkktts.core.constants.OfflineDataStorage
import vn.vistark.nkktts.ui.me_danh_bat.ManHinhMeDanhBat
import vn.vistark.nkktts.ui.thong_tin_me_danh_bat.ManHinhThongTinMeDanhBat
import vn.vistark.nkktts.utils.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class ManHinhDanhSachLoai : AppCompatActivity() {
    val MIN_SPICE_IMAGES = 0
    val MAX_SPICE_IMAGES = 3

    val REQUEST_TAKE_PHOTO = 1122
    val REQUEST_PICK_PHOTO = 2233

    var imageUri: Uri? = null

    lateinit var manager: LocationManager
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    lateinit var pDialog: SweetAlertDialog
    lateinit var adapter: SpiceAdapter
    var pressedMillis = -1L
    var syncLocationManagerTimer: Timer? = null


    var spiceImages: Array<String> = emptyArray()
        set(value) {
            spiceImagesVisibleManager(value)
            field = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.man_hinh_danh_sach_loai)
        hideAllSpiceImages()
        initPreComponents()
        initLocationServices()

        ToolbarBackButton(this).show()
        initDanhSachLoai()
        initEvents()
        initIfNowIsReview()
        supportActionBar?.title = getString(R.string.san_luong_me)
    }

    private fun initIfNowIsReview() {
        if (Hauls.currentHault.timeCollectingNets.isNotEmpty()) {
            mhdslBtnKetThucMe.setBackgroundResource(R.drawable.btn_info)
            mhdslBtnKetThucMe.text = getString(R.string.quay_ve)
            mhdslBtnKetThucMe.setOnClickListener {
                onBackPressed()
            }
            ToolbarBackButton(this).show()
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
                        if (!pDialog.isShowing || pDialog.titleText != getString(R.string.hay_bat_gps)) {
                            pDialog.titleText = getString(R.string.hay_bat_gps)
                            pDialog.show()
                        }
                    }
                } else {
                    runOnUiThread {
                        if (pDialog.isShowing && pDialog.titleText == getString(R.string.hay_bat_gps)) {
                            pDialog.titleText = getString(R.string.dang_xu_ly)
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
            if (spiceImages.size < MIN_SPICE_IMAGES) {
                SimpleNotify.error(
                    this,
                    getString(R.string.vui_long_cung_cap_day_du_anh_ve_loai_nay),
                    ""
                )
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
                val input = islmdEdtSanLuong.text.toString().toFloatOrNull()
                if (input == null) {
                    SimpleNotify.error(this, getString(R.string.san_luong_sai).toUpperCase(), "")
                } else {
                    var isExists = false
                    if (Hauls.currentHault.spices.isNotEmpty()) {
                        // Nếu trong danh sách các loài đã đánh bắt của mẻ đã có loài này rồi, thì tiến hành cập nhật nó
                        for (i in Hauls.currentHault.spices.indices) {
                            if (Hauls.currentHault.spices[i].id == spices.id) {
                                Hauls.currentHault.spices[i].name =
                                    spices.name ?: getString(R.string.tag_khong_ro)
                                Hauls.currentHault.spices[i].weight = input
                                Hauls.currentHault.spices[i].images =
                                    GsonBuilder().create().toJson(spiceImages)
                                isExists = true
                                break
                            }
                        }
                    }

                    if (!isExists) {
                        // Còn nếu nó chưa hề tồn tại, ta tiến hành tạo một đối tượng loài bắt được mới
                        val catchedSpices = CatchedSpices()
                        catchedSpices.id = spices.id
                        catchedSpices.name = spices.name ?: getString(R.string.tag_khong_ro)
                        catchedSpices.weight = input
                        catchedSpices.images = GsonBuilder().create().toJson(spiceImages)

                        // Tiến hành thêm loài này vào danh sách các loài đã bắt được của mẻ
                        Hauls.currentHault.spices = Hauls.currentHault.spices.plus(catchedSpices)
                    }
                    // Tuy nhiên nếu có loài mà chưa có mẻ trước đó, chúng ta tiến hành tạo mới dữ liệu mẻ,
                    // và lấy ID là số tiếp theo trong chuỗi
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
                SimpleNotify.warning(
                    this,
                    getString(R.string.dang_lay_vi_tri).toUpperCase(),
                    getString(R.string.thu_lai_sau_1_giay)
                )
            }
        }

        // Load dữ liệu cũ nếu có
        spiceImages = emptyArray()
        if (Hauls.currentHault.spices.isNotEmpty()) {
            for (spice in Hauls.currentHault.spices) {
                if (spice.id == spices.id) {
                    islmdTenLoai.text = spice.name
                    islmdEdtSanLuong.setText(spice.weight.toString())
                    spiceImages =
                        GsonBuilder().create().fromJson(spice.images, Array<String>::class.java)
                    return
                }
            }
        }
    }

    fun hideBottomSheetCapNhatSanLuong() {
        try {
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                this.currentFocus!!.windowToken,
                0
            )
        } catch (var2: Exception) {
        }
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
                onBackPressed()
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
                    SimpleNotify.error(
                        this,
                        getString(R.string.ket_thuc_loi).toUpperCase(),
                        getString(R.string.vui_long_thu_lai)
                    )
                }
            } else {
                SimpleNotify.warning(
                    this,
                    getString(R.string.dang_lay_vi_tri),
                    getString(R.string.thu_lai_sau_x_giay_hoac_lau_hon_neu_gps_kem)
                )
            }
        }
    }

    private fun initPreComponents() {
        // Progress dialog
        pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog.titleText = getString(R.string.dang_xu_ly)
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
                getString(R.string.lay_loai_loi).toUpperCase(),
                getString(R.string.vui_long_thu_lai)
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

    private fun stopTimer() {
        syncLocationManagerTimer?.cancel()
        syncLocationManagerTimer = null
    }

    private fun hideAllSpiceImages() {
        for (i in 1..6) {
            val v = getSpicesImageView(i)
            v.setImageResource(R.drawable.add_photo)
            if (i > 1) {
                v.visibility = View.GONE
            }
            v.setOnClickListener {
                selectImage()
            }
        }
    }

    fun spiceImagesVisibleManager(sis: Array<String>) {
        hideAllSpiceImages()
        for (i in sis.indices) {
            // current image
            val currentImage = getSpicesImageView(i + 1)
            currentImage.visibility = View.VISIBLE
//            currentImage.setImageURI(Uri.fromFile(File(sis[i])))
            val bm = FileUtils.getBitmapFile(sis[i])
            if (bm != null) {
                currentImage.setImageBitmap(bm)
            } else {
                currentImage.setImageResource(R.drawable.add_photo)
            }
            // next image if have
            if (i + 2 <= MAX_SPICE_IMAGES) {
                val nextImage = getSpicesImageView(i + 2)
                nextImage.visibility = View.VISIBLE
            }
        }
    }

    fun getSpicesImageView(num: Int): ImageView {
        return this.findViewById(
            resources.getIdentifier(
                "spice_img_${num}",
                "id",
                packageName
            )
        )
    }


    private fun selectImage() {
        val options =
            arrayOf<CharSequence>(
                getString(R.string.chup_anh), getString(R.string.chon_anh), getString(
                    R.string.dong
                )
            )
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.chon_phuong_thuc_lay_anh))
        builder.setItems(options) { dialog, index ->
            when (index) {
                0 -> {
                    val values = ContentValues()
                    values.put(
                        MediaStore.Images.Media.TITLE,
                        "Vistark_${System.currentTimeMillis()}"
                    )
                    values.put(
                        MediaStore.Images.Media.DESCRIPTION,
                        "Write new app? contact projects.futuresky@gmail.com"
                    )
                    imageUri = contentResolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
                    );
                    val takePicture =
                        Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    takePicture.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                    startActivityForResult(takePicture, REQUEST_TAKE_PHOTO)
                }
                1 -> {
                    val pickPhoto = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )
                    startActivityForResult(pickPhoto, REQUEST_PICK_PHOTO)
                }
                2 -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_CANCELED) {
            when (requestCode) {
                REQUEST_TAKE_PHOTO -> if (resultCode == Activity.RESULT_OK) {
                    if (imageUri != null) {
                        imageUriProcessing(imageUri!!, true)
                    }
                }
                REQUEST_PICK_PHOTO -> if (resultCode == Activity.RESULT_OK) {
                    val selectedImage: Uri? = data?.data
                    if (selectedImage != null) {
                        imageUriProcessing(selectedImage)
                    }
                }
            }
        }
    }

    private fun imageUriProcessing(uri: Uri, isDelete: Boolean = false) {
        // Copy sang thư mục cache
        val bm = FileUtils.getCapturedImage(this, uri)
        val s = FileUtils.SaveSpiceImages(this, "spices", bm)
        spiceImages = spiceImages.plus(s)
//        println(GsonBuilder().create().toJson(spiceImages))
        // Xóa file nếu có yêu cầu
        if (isDelete) {
            val path = uri.path
            if (path != null) {
                val f = File(path)
                if (f.exists()) {
                    f.delete()
                }
            }
        }
    }

    fun back() {
        startActivity(Intent(this@ManHinhDanhSachLoai, ManHinhMeDanhBat::class.java))
        ToolbarBackButton(this@ManHinhDanhSachLoai).overrideAnimationOnEnterAndExitActivityReveret()
        finish()
    }

    override fun onBackPressed() {
        // Nếu xem lại
        if (Hauls.currentHault.timeCollectingNets.isNotEmpty()) {
            back()
        } else {
            // Nếu mẻ chưa có sản lượng
            if (Hauls.currentHault.spices.isEmpty()) {
                back()
            } else {
                SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE).apply {
                    titleText = getString(R.string.bo_qua_tat_ca_du_lieu_hien_tai)
                    setConfirmButton(getString(R.string.dong_y)) {
                        it.dismiss()
                        Hauls.removeHault()
                        Constants.updateCurrentTrip()
                        back()
                    }
                    setCancelButton(getString(R.string.huy)) {
                        it.dismiss()
                    }
                    show()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

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
