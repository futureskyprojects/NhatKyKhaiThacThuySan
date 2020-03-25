package vn.vistark.nkktts.core.constants

import UserInfo
import android.content.SharedPreferences
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
            } else {
                return false
            }
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