package vn.vistark.nkktts.core.services

import SyncSuccess
import TheTripStorage
import android.content.Intent
import android.os.AsyncTask
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.vistark.nkktts.R
import vn.vistark.nkktts.core.api.APIUtils
import vn.vistark.nkktts.core.constants.Constants
import vn.vistark.nkktts.ui.khoi_tao_chuyen_di_bien.ManHinhKhoiTaoChuyenDiBien
import vn.vistark.nkktts.utils.SimpleNotify
import java.lang.Exception

class SyncTrip(val theTripStorage: TheTripStorage) : AsyncTask<Void, Void, Int>() {
    override fun doInBackground(vararg params: Void?): Int {
        try {
            val syncSuccess = APIUtils.mAPIServices?.syncTrip(theTripStorage)?.execute()
            if (syncSuccess != null && syncSuccess.isSuccessful && syncSuccess.body() != null) {
                val syss = syncSuccess.body()!!.status
                return syss
            } else {
                return syncSuccess!!.code()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return -1
    }
}