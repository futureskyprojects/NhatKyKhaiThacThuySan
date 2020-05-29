package vn.vistark.nkktts.ui.thong_tin_me_danh_bat

import Hauls
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.man_hinh_thong_tin_me_danh_bat.*
import vn.vistark.nkktts.R
import vn.vistark.nkktts.core.constants.Constants
import vn.vistark.nkktts.ui.danh_sach_loai.ManHinhDanhSachLoai
import vn.vistark.nkktts.ui.me_danh_bat.ManHinhMeDanhBat
import vn.vistark.nkktts.utils.DateTimeUtils
import vn.vistark.nkktts.utils.LocationUtils
import vn.vistark.nkktts.utils.SimpleNotify
import vn.vistark.nkktts.utils.ToolbarBackButton


class ManHinhThongTinMeDanhBat : AppCompatActivity() {
    var googleMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.man_hinh_thong_tin_me_danh_bat)

        ToolbarBackButton(this).show()

        initDataShow()

        intEvents()
        supportActionBar?.title = getString(R.string.thong_tin_me)

        initMaps(savedInstanceState)
    }

    @SuppressLint("SetTextI18n")
    private fun initDataShow() {
        if (Hauls.currentHault.orderNumber < 0) {
            onBackPressed()
        } else {
            var calSum = 0F
            for (spice in Hauls.currentHault.spices) {
                calSum += spice.weight
            }
            mhttmdbTvThoiGianTha.text = Hauls.currentHault.timeDropNets
            mhttmdbTvViTha.text =
                "${Hauls.currentHault.latDrop},${Hauls.currentHault.lngDrop}\n(${LocationUtils.convert(
                    Hauls.currentHault.latDrop,
                    Hauls.currentHault.lngDrop
                )})"
            if (Hauls.currentHault.timeCollectingNets.isNotEmpty()) {
                mhttmdbTvThoiGianThu.text = Hauls.currentHault.timeCollectingNets
                SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE).apply {
                    titleText =
                        getString(R.string.xac_nhan_thay_the_thoi_gian_thu)
                    contentText = getString(R.string.cap_nhat)
                    setConfirmButton(getString(R.string.dong_y)) {
                        it.dismissWithAnimation()
                        this@ManHinhThongTinMeDanhBat.mhttmdbTvThoiGianThu.text =
                            DateTimeUtils.getStringCurrentYMDHMS()
                    }
                    setCancelButton(getString(R.string.van_giu)) {
                        it.dismissWithAnimation()
                    }
                    show()
                }
            } else {
                mhttmdbTvThoiGianThu.text = DateTimeUtils.getStringCurrentYMDHMS()
            }
            mhttmdbTvViTriThu.text =
                "${Hauls.currentHault.latCollecting},${Hauls.currentHault.lngCollecting}\n(${LocationUtils.convert(
                    Hauls.currentHault.latCollecting,
                    Hauls.currentHault.lngCollecting
                )})"
            mhttmdbTvTongSanLuong.text =
                "${getString(R.string.tong_san_luong_tan).toUpperCase()}: ${(calSum / 1000)}"
        }
    }

    private fun intEvents() {
        mhttmdbBtnLuu.setOnClickListener {
            Hauls.currentHault.timeCollectingNets =
                mhttmdbTvThoiGianThu.text.toString()
            Hauls.updateHault()
            if (Constants.updateCurrentTrip()) {
                val manHinhMeDanhBatIntent = Intent(this, ManHinhMeDanhBat::class.java)
                startActivity(manHinhMeDanhBatIntent)
                finish()
            } else {
                SimpleNotify.warning(
                    this,
                    getString(R.string.luu_khong_duoc),
                    getString(R.string.vui_long_thu_lai)
                )
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        SimpleNotify.onBackConfirm(this) {
            val manHinhDanhSachLoaiIntent = Intent(this, ManHinhDanhSachLoai::class.java)
            startActivity(manHinhDanhSachLoaiIntent)
            finish()
            super.onBackPressed()
        }
    }


    private fun initMaps(savedInstanceState: Bundle?) {
        mapView.onCreate(savedInstanceState)
        mapView.onResume()
        MapsInitializer.initialize(this)

        val dropPosition = LatLng(
            Hauls.currentHault.latDrop.toDoubleOrNull()
                ?: return,
            Hauls.currentHault.lngDrop.toDoubleOrNull()
                ?: return
        )

        val collectPosition = LatLng(
            Hauls.currentHault.latCollecting.toDoubleOrNull()
                ?: return,
            Hauls.currentHault.lngCollecting.toDoubleOrNull()
                ?: return
        )
        mapView.getMapAsync { gMaps ->
            googleMap = gMaps
            googleMap?.setOnMapLoadedCallback {
                googleMap!!.clear()
                // Vị trí thả
                googleMap!!.addMarker(
                    MarkerOptions()
                        .icon(
                            BitmapDescriptorFactory.fromResource(R.drawable.placeholder_a)
                        )
                        .position(
                            dropPosition
                        )
                        .draggable(false)
                        .title("${Hauls.currentHault.timeDropNets}")
                        .snippet(getString(R.string.vi_tri_tha))
                )
                // Vị trí thu
                googleMap!!.addMarker(
                    MarkerOptions()
                        .icon(
                            BitmapDescriptorFactory.fromResource(R.drawable.placeholder_b)
                        )
                        .position(
                            collectPosition
                        )
                        .draggable(false)
                        .title("${Hauls.currentHault.timeCollectingNets}")
                        .snippet(getString(R.string.vi_tri_thu))
                )
                googleMap!!.mapType = GoogleMap.MAP_TYPE_SATELLITE
                googleMap!!.uiSettings.isZoomControlsEnabled = true
                googleMap!!.isMyLocationEnabled = false

                // Move to mid point

                val center = LocationUtils.midPoint(dropPosition, collectPosition)
                val camUp = CameraUpdateFactory.newLatLngZoom(center, 9.5F)
                googleMap!!.animateCamera(camUp)
            }
        }
    }

}
