package vn.vistark.nkktts.utils

import android.Manifest
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng


class LocationUtils(val mContext: AppCompatActivity) : GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    LocationListener {

    companion object {
        fun convert(
            latitude: String,
            longitude: String
        ): String {
            return convert(
                latitude.toDoubleOrNull() ?: return "",
                longitude.toDoubleOrNull() ?: return ""
            )
        }

        fun convert(
            latitude: Double,
            longitude: Double
        ): String {
            val builder = StringBuilder()
            if (latitude < 0) {
                builder.append("S ")
            } else {
                builder.append("N ")
            }
            val latitudeDegrees = Location.convert(
                Math.abs(latitude),
                Location.FORMAT_SECONDS
            )
            val latitudeSplit =
                latitudeDegrees.split(":").toTypedArray()
            builder.append(latitudeSplit[0])
            builder.append("°")
            builder.append(latitudeSplit[1])
            builder.append("'")
            builder.append(latitudeSplit[2])
            builder.append("\"")
            builder.append(" ")
            if (longitude < 0) {
                builder.append("W ")
            } else {
                builder.append("E ")
            }
            val longitudeDegrees = Location.convert(
                Math.abs(longitude),
                Location.FORMAT_SECONDS
            )
            val longitudeSplit =
                longitudeDegrees.split(":").toTypedArray()
            builder.append(longitudeSplit[0])
            builder.append("°")
            builder.append(longitudeSplit[1])
            builder.append("'")
            builder.append(longitudeSplit[2])
            builder.append("\"")
            return builder.toString()
        }

        fun midPoint(A: LatLng, B: LatLng): LatLng {
            val phi1 = Math.toRadians(A.latitude)
            val gamma1 = Math.toRadians(A.longitude)
            val phi2 = Math.toRadians(B.latitude)
            val deltaGamma = Math.toRadians(B.longitude - A.longitude)
            val aux1 = Math.cos(phi2) * Math.cos(deltaGamma)
            val aux2 = Math.cos(phi2) * Math.sin(deltaGamma)
            val x =
                Math.sqrt((Math.cos(phi1) + aux1) * (Math.cos(phi1) + aux1) + aux2 * aux2)
            val y = Math.sin(phi1) + Math.sin(phi2)
            val phi3 = Math.atan2(y, x)
            val gamma3 =
                gamma1 + Math.atan2(aux2, Math.cos(phi1) + aux1)

            // normalise to −180..+180°
            return LatLng(
                Math.toDegrees(phi3),
                (Math.toDegrees(gamma3) + 540) % 360 - 180
            )
        }
    }

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