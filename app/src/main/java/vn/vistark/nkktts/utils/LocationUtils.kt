package vn.vistark.nkktts.utils

import android.Manifest
import android.R
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices


class LocationUtils(val mContext: AppCompatActivity) : GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    LocationListener {

    init {
        initGPSLocation()
    }

    private val UPDATE_INTERVAL: Long = 5000
    private val FASTEST_INTERVAL: Long = 5000

    var mGoogleApiClient: GoogleApiClient? = null
    private var mLocationRequest: LocationRequest? = null
    var mLastLocation: Location? = null

    private fun initGPSLocation() {
        if (isPlayServicesAvailable()) {
            setUpLocationClientIfNeeded()
            buildLocationRequest()
        } else {
            SimpleNotify.error(mContext, "LỖI DỊCH VỤ VỊ TRÍ", "Điện thoại không hỗ trợ", false)
        }
    }

    private fun isPlayServicesAvailable(): Boolean {
        return (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(mContext)
                == ConnectionResult.SUCCESS)
    }

    private fun isGpsOn(): Boolean {
        val manager =
            mContext.getSystemService(LOCATION_SERVICE) as LocationManager
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun setUpLocationClientIfNeeded() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
        }
    }

    private fun buildLocationRequest() {
        mLocationRequest = LocationRequest.create()
        mLocationRequest?.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        mLocationRequest?.setInterval(UPDATE_INTERVAL)
        mLocationRequest?.setFastestInterval(FASTEST_INTERVAL)
    }

    fun startLocationUpdates() {
        if (mGoogleApiClient!!.isConnected) LocationServices.FusedLocationApi.requestLocationUpdates(
            mGoogleApiClient, mLocationRequest, this
        )
    }

    fun stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this)
    }

    //=================================================

    override fun onConnected(p0: Bundle?) {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                mContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            SimpleNotify.error(mContext, "CHƯA CẤP QUYỀN VỊ TRÍ", "Cấp và khởi động lại", false)
            return
        }
        val lastLocation =
            LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)
        if (lastLocation != null) {
            mLastLocation = lastLocation
        }
    }

    override fun onConnectionSuspended(p0: Int) {
        mGoogleApiClient?.connect()
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
    }

    override fun onLocationChanged(location: Location?) {
        mLastLocation = location
    }

}