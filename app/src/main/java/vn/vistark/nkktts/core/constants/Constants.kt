package vn.vistark.nkktts.core.constants

import TheTripStorage
import UserInfo
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import vn.vistark.nkktts.core.models.selected_job.SelectedJob

class Constants {
    companion object {
        class DataKey {
            companion object {
                val selectedJob: String = "selectedJob"
                var userId: String = "userId"
                var userToken: String = "userToken"
                var tokenType: String = "tokenType"
                var userInfo: String = "userInfo"
            }
        }

        var sharedPreferences: SharedPreferences? = null
        var userId: String = ""
        var userToken: String = ""
        var tokenType: String = ""
        var userInfo: UserInfo = UserInfo()
        var selectedJob: SelectedJob = SelectedJob()
        var currentTrip: TheTripStorage = TheTripStorage()

        fun logOut(): Boolean {
            userId = ""
            userToken = ""
            tokenType = ""
            userInfo = UserInfo()
            return updateAll()
        }

        fun readAllSavedData(): Boolean {
            if (sharedPreferences != null) {
                userId = sharedPreferences?.getString(DataKey.userId, "")!!
                userToken = sharedPreferences?.getString(DataKey.userToken, "")!!
                tokenType = sharedPreferences?.getString(DataKey.tokenType, "")!!
                val tempUserInfo = sharedPreferences?.getString(DataKey.userInfo, "")
                if (!tempUserInfo.isNullOrEmpty()) {
                    userInfo = GsonBuilder().create().fromJson(tempUserInfo, UserInfo::class.java)
                }
                val tempSelectedJob = sharedPreferences?.getString(DataKey.selectedJob, "")
                if (!tempSelectedJob.isNullOrEmpty()) {
                    selectedJob =
                        GsonBuilder().create().fromJson(tempSelectedJob, SelectedJob::class.java)
                }
                currentTrip = OfflineDataStorage.get<TheTripStorage>(OfflineDataStorage.trip)
                    ?: TheTripStorage()
                return true
            } else {
                return false
            }
        }

        fun updateAll(): Boolean {
            if (sharedPreferences != null) {
                return updateUserId()
                        && updateUserToken()
                        && updateTokenType()
                        && updateUserInfo()
                        && updateSelectedJob()
                        && updateCurrentTrip()
            } else {
                return false
            }
        }

        fun updateCurrentTrip(): Boolean {
            Log.w("TRIP", GsonBuilder().create().toJson(currentTrip))
            return OfflineDataStorage.saveData(OfflineDataStorage.trip, currentTrip)
        }

        fun updateUserId(): Boolean {
            if (sharedPreferences != null) {
                return sharedPreferences!!.edit().putString(DataKey.userId, userId).commit()
            } else {
                return false
            }
        }

        fun updateUserToken(): Boolean {
            if (sharedPreferences != null) {
                return sharedPreferences!!.edit().putString(DataKey.userToken, userToken).commit()
            } else {
                return false
            }
        }

        fun updateTokenType(): Boolean {
            if (sharedPreferences != null) {
                return sharedPreferences!!.edit().putString(DataKey.tokenType, tokenType).commit()
            } else {
                return false
            }
        }

        fun updateUserInfo(): Boolean {
            if (sharedPreferences != null) {
                val tempUserInfo: String = GsonBuilder().create().toJson(userInfo)
                return sharedPreferences!!.edit().putString(DataKey.userInfo, tempUserInfo).commit()
            } else {
                return false
            }
        }

        fun updateSelectedJob(): Boolean {
            if (sharedPreferences != null) {
                val tempSelectedJob: String = GsonBuilder().create().toJson(selectedJob)
                return sharedPreferences!!.edit().putString(DataKey.selectedJob, tempSelectedJob)
                    .commit()
            } else {
                return false
            }
        }

        fun isLoggedIn(): Boolean {
            return (tokenType.isNotEmpty() && userToken.isNotEmpty())
        }

        fun isCreatingNewHaul(): Boolean {
            if (currentTrip.trip.hauls.isNotEmpty()) {
                currentTrip.trip.hauls.forEach { haul ->
                    Log.w("HAUL-CHECK", GsonBuilder().create().toJson(haul))
                    if (haul.timeCollectingNets.isEmpty() || haul.latCollecting.isEmpty() || haul.lngCollecting.isEmpty()) {
                        Hauls.currentHault = haul
                        Log.w("HAUL-CHECK", "TRUEEEEEEEEEEEEE")
                        return true
                    }
                }
                return false
            } else {
                return false
            }
        }

        fun isSelectedDeparturePortAndStarted(): Boolean {
            return (currentTrip.trip.departurePort >= 0 && currentTrip.trip.departureTime.isNotEmpty())
        }

        fun isSelectedJob(): Boolean {
            if (sharedPreferences != null) {
                if (selectedJob.jobId >= 0 && selectedJob.jobInfo.isNotEmpty()) {
                    for (info in selectedJob.jobInfoArray) {
                        if (info <= 0)
                            return false
                    }
                    return true
                } else {
                    return false
                }
            } else {
                return false
            }
        }
    }
}