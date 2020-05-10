package vn.vistark.nkktts.ui.khoi_dong

import GetSelectedJobResponse
import ProfileResponse
import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.vistark.nkktts.R
import vn.vistark.nkktts.core.api.APIUtils
import vn.vistark.nkktts.core.constants.Constants
import vn.vistark.nkktts.core.services.SyncService
import vn.vistark.nkktts.ui.danh_sach_loai.ManHinhDanhSachLoai
import vn.vistark.nkktts.ui.danh_sach_nghe.ManHinhDanhSachNghe
import vn.vistark.nkktts.ui.khoi_tao_chuyen_di_bien.ManHinhKhoiTaoChuyenDiBien
import vn.vistark.nkktts.ui.dang_nhap.ManHinhDangNhap
import vn.vistark.nkktts.ui.me_danh_bat.ManHinhMeDanhBat
import vn.vistark.nkktts.ui.thiet_lap.SyncAvatar
import vn.vistark.nkktts.utils.DataInitialize
import vn.vistark.nkktts.utils.LanguageChange
import vn.vistark.nkktts.utils.SimpleNotify
import java.util.*


class ManHinhKhoiDong : AppCompatActivity() {
    val TAG = ManHinhKhoiDong::class.java.simpleName
    lateinit var pDialog: SweetAlertDialog
    private var DONT_NEED_TO_LOAD_OFFLINE_DATAS = false

    val appPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )
    } else {
        arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.man_hinh_khoi_dong)
        DONT_NEED_TO_LOAD_OFFLINE_DATAS =
            intent.getBooleanExtra("DONT_NEED_TO_LOAD_OFFLINE_DATAS", false)

        // Đổi ngôn ngữ
        LanguageChange.onChange(baseContext)

        initPreComponents()
        initPre()
        if (!DONT_NEED_TO_LOAD_OFFLINE_DATAS) {
            DataInitialize() // Fetch data from internet
            // Khởi động services
            runServices()
        }
        permissionRequest()
    }

    private fun initPreComponents() {
        // Progress dialog
        pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog.titleText = getString(R.string.hay_bat_gps).toUpperCase()
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
//                            SimpleNotify.error(this@ManHinhKhoiDong, "Oops...", "Lấy cảng lỗi")
                        } else if (!DataInitialize.isInitSpiceSuccess) {
                            Log.w(TAG, "Chưa hoàn tất fetch dữ liệu loài")
                            DataInitialize() // Lấy lại lần nữa
//                            SimpleNotify.error(this@ManHinhKhoiDong, "Oops...", "Lấy loài lỗi")
                        } else if (!DataInitialize.isInitJobSuccess) {
                            Log.w(TAG, "Chưa hoàn tất fetch dữ liệu nghề")
                            DataInitialize() // Lấy lại lần nữa
//                            SimpleNotify.error(this@ManHinhKhoiDong, "Oops...", "Lấy nghề lỗi")
                        } else {
                            Log.w(TAG, "Đã hoàn tất fetch dữ liệu từ Internet")
                            this.cancel()
                            checkLoginAndRouting()
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
        Constants.sharedPreferencesForOfflineData =
            getSharedPreferences(
                application.packageName.toUpperCase() + "_FOR_SAVE_OFFLINE_DATA",
                Context.MODE_PRIVATE
            )
    }

    private fun checkLoginAndRouting() {
        if (Constants.sharedPreferences == null) {
            SimpleNotify.error(
                this,
                getString(R.string.loi_du_lieu).toUpperCase(),
                getString(R.string.khoi_tao_trinh_doc_du_lieu_khong_duoc)
            )
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    checkLoginAndRouting()
                    finish()
                }
            }, 500)
        } else {
//            TripHistory.getHistory()
            Constants.readAllSavedData()
            // Kiểm tra xem đã đăng nhập chưa
            if (Constants.isLoggedIn()) {
                getUserProfile()
                getSelectedJob()
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
        var isFullGranted = true
        appPermissions.forEach {
            if (ContextCompat.checkSelfPermission(
                    this,
                    it
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                isFullGranted = false
                return@forEach
            }
        }
        if (!isFullGranted) {
            ActivityCompat.requestPermissions(
                this,
                appPermissions,
                1234
            )
        } else {
            if (DONT_NEED_TO_LOAD_OFFLINE_DATAS) {
                checkLoginAndRouting()
            } else {
                startTimer()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1234 -> {
                var isFullGranted = true
                grantResults.forEach {
                    if (it != PackageManager.PERMISSION_GRANTED) {
                        isFullGranted = false
                        return@forEach
                    }
                }
                if (grantResults.size >= appPermissions.size && isFullGranted) {
                    if (DONT_NEED_TO_LOAD_OFFLINE_DATAS) {
                        checkLoginAndRouting()
                    } else {
                        startTimer()
                    }
                } else {
                    permissionRequest()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun getUserProfile() {
        APIUtils.mAPIServices?.profileAPI()?.enqueue(object : Callback<ProfileResponse> {
            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
            }

            override fun onResponse(
                call: Call<ProfileResponse>,
                response: Response<ProfileResponse>
            ) {
                if (response.isSuccessful) {
                    val profileResponse = response.body()?.profile
                    if (profileResponse != null) {
                        Constants.userId = profileResponse.id.toString()
                        Constants.userInfo.shipOwner = profileResponse.shipOwner
                        Constants.userInfo.captain = profileResponse.captain
                        Constants.userInfo.shipNumber = profileResponse.shipNumber
                        Constants.userInfo.lengthShip = profileResponse.lengthShip.toString()
                        Constants.userInfo.power = profileResponse.power.toString()
                        Constants.userInfo.fishingLicense = profileResponse.fishingLicense
                        Constants.userInfo.duration = profileResponse.duration
                        Constants.userInfo.secondJob = profileResponse.secondJob
                        Constants.userInfo.phone = profileResponse.phone
                        Constants.userInfo.status = profileResponse.status.toString()
                        Constants.userInfo.createAt = profileResponse.createdAt
                        Constants.userInfo.updateAt = profileResponse.updatedAt

                        // Sync avatar từ server
                        if (!profileResponse.image.isNullOrEmpty()) {
                            if (profileResponse.image != Constants.userInfo.image || !vn.vistark.nkktts.utils.FileUtils.isAvatarExists(
                                    this@ManHinhKhoiDong
                                )
                            ) {
                                Constants.userInfo.image = profileResponse.image
                                println("Tiến hành đồng bộ avatar từ server")
                                SyncAvatar.syncFromServerAsync(
                                    this@ManHinhKhoiDong,
                                    Constants.userInfo.image!!
                                ).execute()
                            }
                        } else {
                            println("Không có avatar nên tiến hành xóa đi")
                            vn.vistark.nkktts.utils.FileUtils.removeAvatar(this@ManHinhKhoiDong)
                        }
                        Constants.updateUserInfo()
                        return
                    }
                }
            }
        })
    }

    private fun getSelectedJob() {
        APIUtils.mAPIServices?.getSelectedJobAPI()?.enqueue(object :
            Callback<GetSelectedJobResponse> {
            override fun onFailure(call: Call<GetSelectedJobResponse>, t: Throwable) {
                t.printStackTrace()
                appRouting()
            }

            override fun onResponse(
                call: Call<GetSelectedJobResponse>,
                response: Response<GetSelectedJobResponse>
            ) {
                if (response.isSuccessful) {
                    val selectedJob =
                        if (response.body()?.data != null && response.body()!!.data.isNotEmpty()) response.body()?.data?.first() else null
                    if (selectedJob != null) {
                        val jis = selectedJob.infoJob!!.replace("\\[|\\]".toRegex(), "").split(",")
                        Constants.selectedJob.id = selectedJob.id!!
                        Constants.selectedJob.jobId = selectedJob.jobId!!
                        Constants.selectedJob.jobInfoArray =
                            listOf(jis[0].trim().toFloat(), jis[1].trim().toFloat())
                        Constants.selectedJob.jobInfo = selectedJob.infoJob
                        Constants.selectedJob.captainId = selectedJob.captainId!!
                        Constants.updateSelectedJob()
                    }
                } else {
                }
                appRouting()
            }
        })
    }

    fun appRouting() {
        // Tiến hành routing
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
    }

    private fun checkServiceRunning(): Boolean {
        val manager =
            getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (SyncService::class.java.name == service.service.className) {
                return true
            }
        }
        return false
    }

    private fun runServices() {
        if (!checkServiceRunning()) {
            println("Services chưa chạy, tiến hành chạy services mới >>>>>>>>>>>>")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(
                    Intent(
                        this,
                        SyncService::class.java
                    )
                )
            } else {
                startService(Intent(this, SyncService::class.java))
            }
        } else {
            println("Services đã chạy >>>>>>>>>>>>>>>>>>>")
        }
    }
}
