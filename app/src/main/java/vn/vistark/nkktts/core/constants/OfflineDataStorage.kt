package vn.vistark.nkktts.core.constants

import com.google.gson.Gson
import com.google.gson.GsonBuilder

class OfflineDataStorage {
    companion object {
        val jobs = "JOB_OFFLINE_DATA_STORAGE"
        val seaPorts = "SEA_PORTS_OFFLINE_DATA_STORAGE"
        val spices = "SPICES_OFFLINE_DATA_STORAGE"

        fun <T> saveData(dataKey: String, `object`: T): Boolean {
            if (Constants.sharedPreferences != null) {
                val jsonString = GsonBuilder().create().toJson(`object`)
                return Constants.sharedPreferences?.edit()?.putString(
                    dataKey,
                    jsonString
                )!!.commit()
            } else {
                return false
            }
        }

        inline fun <reified T> get(key: String): T? {
            if (Constants.sharedPreferences != null) {
                val jsonString = Constants.sharedPreferences?.getString(key, null)
                if (jsonString != null) {
                    return GsonBuilder().create().fromJson(jsonString, T::class.java)
                }
            }
            return null

        }
    }
}