package vn.vistark.nkktts.ui.man_hinh_khoi_dong

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import vn.vistark.nkktts.R
import vn.vistark.nkktts.core.constants.Constants
import vn.vistark.nkktts.core.models.trip_history.TripHistory
import vn.vistark.nkktts.ui.danh_sach_loai.ManHinhDanhSachLoai
import vn.vistark.nkktts.ui.danh_sach_nghe.ManHinhDanhSachNghe
import vn.vistark.nkktts.ui.khoi_tao_chuyen_di_bien.ManHinhKhoiTaoChuyenDiBien
import vn.vistark.nkktts.ui.man_hinh_dang_nhap.ManHinhDangNhap
import vn.vistark.nkktts.ui.me_danh_bat.ManHinhMeDanhBat
import vn.vistark.nkktts.utils.DataInitialize
import vn.vistark.nkktts.utils.SimpleNotify
import java.util.*


class ManHinhKhoiDong : AppCompatActivity() {
    val TAG = ManHinhKhoiDong::class.java.simpleName
    lateinit var pDialog: SweetAlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.man_hinh_khoi_dong)

        initPreComponents()
        initPre()
        DataInitialize() // Fetch data from internet
        permissionRequest()
    }

    private fun initPreComponents() {
        // Progress dialog
        pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog.titleText = "Hãy bật GPS"
        pDialog.setCancelable(false)
    }

    fun startTimer() {
        val manager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        Timer().schedule(object : TimerTask() {
            override fun run() {
                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    runOnUiThread {
                        if (!pDialog.isShowing) {
                            pDialog.show()
                        }
                    }
                } else {
                    runOnUiThread {
                        if (pDialog.isShowing) {
                            pDialog.dismissWithAnimation()
                        }
                    }
                    if (DataInitialize.isFinished()) {
                        if (!DataInitialize.isInitSeaPortSuccess) {
                            Log.w(TAG, "Chưa hoàn tất fetch dữ liệu cảng biển")
                            DataInitialize() // Lấy lại lần nữa
                            SimpleNotify.error(this@ManHinhKhoiDong, "Oops...", "Lấy cảng lỗi")
                        } else if (!DataInitialize.isInitSpiceSuccess) {
                            Log.w(TAG, "Chưa hoàn tất fetch dữ liệu loài")
                            DataInitialize() // Lấy lại lần nữa
                            SimpleNotify.error(this@ManHinhKhoiDong, "Oops...", "Lấy loài lỗi")
                        } else if (!DataInitialize.isInitJobSuccess) {
                            Log.w(TAG, "Chưa hoàn tất fetch dữ liệu nghề")
                            DataInitialize() // Lấy lại lần nữa
                            SimpleNotify.error(this@ManHinhKhoiDong, "Oops...", "Lấy nghề lỗi")
                        } else {
                            Log.w(TAG, "Đã hoàn tất fetch dữ liệu từ Internet")
                            this.cancel()
                            chuyenQuaManHinhDangNhap()
                        }
                    } else {
                        Log.w(TAG, "Đang fetch dữ liệu mới từ mạng")
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
            TripHistory.getHistory()
            Constants.readAllSavedData()
            // Kiểm tra xem đã đăng nhập chưa
            if (Constants.isLoggedIn()) {
                if (Constants.isSelectedJob()) {
                    if (Constants.isSelectedDeparturePortAndStarted()) {
                        if (Constants.isCreatingNewHaul()) {
                            Log.w(TAG, "Vào màn hình danh sách loài")
                            val intent = Intent(this, ManHinhDanhSachLoai::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Log.w(TAG, "Vào mà hình tạo mẻ mới")
                            val manHinhMeDanhBatIntent = Intent(this, ManHinhMeDanhBat::class.java)
                            startActivity(manHinhMeDanhBatIntent)
                            finish()
                        }
                    } else {
                        Log.w(TAG, "Vào màn hình khởi tạo chuyến đi biển")
                        val ktcdbIntent =
                            Intent(
                                this,
                                ManHinhKhoiTaoChuyenDiBien::class.java
                            )
                        startActivity(ktcdbIntent)
                        finish()
                    }
                } else {
                    Log.w(TAG, "Vào màn hình chọn nghề")
                    // Vào màn hình chọn nghề khi chưa chọn
                    startActivity(Intent(this, ManHinhDanhSachNghe::class.java))
                    finish()
                }
            } else {
                Log.w(TAG, "Vào màn hình đăng nhập")
                // Vào màn hình đăng nhập
                startActivity(Intent(this, ManHinhDangNhap::class.java))
                finish()
            }
        }
    }

    private fun permissionRequest() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                1234
            )
        } else {
            startTimer()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1234 -> {
                if (grantResults.size >= 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    startTimer()
                } else {
                    permissionRequest()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
