package vn.vistark.nkktts.utils

import GetJobsResponse
import SeaPortsReponse
import SpicesResponse
import android.util.Log
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.vistark.nkktts.core.api.APIUtils
import vn.vistark.nkktts.core.constants.OfflineDataStorage

class DataInitialize() {

    init {
        isInitSeaPort = true
        isInitSeaPortSuccess = false
        isInitSpices = true
        isInitSpiceSuccess = false
        isInitJobs = true
        isInitJobSuccess = false
        initSeaPorts()
        initSpices()
        initJobs()
    }

    companion object {
        val TAG = DataInitialize::class.java.simpleName

        var isInitSeaPort = true
        var isInitSeaPortSuccess = false

        var isInitSpices = true
        var isInitSpiceSuccess = false

        var isInitJobs = true
        var isInitJobSuccess = false

        fun isFinished(): Boolean {
            return !(isInitSeaPort || isInitSpices || isInitJobs)
        }

        fun initSeaPorts() {
            Log.w(TAG, "Đang tiến hành lấy dữ liệu cảng")
            APIUtils.mAPIServices?.getSeaPorts()?.enqueue(object : Callback<SeaPortsReponse> {
                override fun onFailure(call: Call<SeaPortsReponse>, t: Throwable) {
                    isInitSeaPortSuccess =
                        OfflineDataStorage.get<SeaPortsReponse>(OfflineDataStorage.seaPorts) != null
                    isInitSeaPort = false
                    Log.w(TAG, "Lấy online không được, tiến hành lấy offline cảng")
                }

                override fun onResponse(
                    call: Call<SeaPortsReponse>,
                    response: Response<SeaPortsReponse>
                ) {
                    if (response.isSuccessful) {
                        val seaPortsReponse = response.body()
                        if (seaPortsReponse != null) {
                            Log.w(TAG, "Đã lấy thành công cảng biển")
                            OfflineDataStorage.saveData(
                                OfflineDataStorage.seaPorts,
                                seaPortsReponse
                            )
                            isInitSeaPortSuccess = true
                            isInitSeaPort = false
                            return
                        }
                    }
                    Log.w(TAG, "Lấy cảng offline")
                    // Khi lấy không thành công
                    isInitSeaPortSuccess =
                        OfflineDataStorage.get<SeaPortsReponse>(OfflineDataStorage.seaPorts) != null
                    isInitSeaPort = false
                }
            })
        }

        fun initSpices() {
            Log.w(TAG, "Tiến hành lấy danh sách loài")
            APIUtils.mAPIServices?.getSpices()?.enqueue(object : Callback<SpicesResponse> {
                override fun onFailure(call: Call<SpicesResponse>, t: Throwable) {
                    isInitSpiceSuccess =
                        OfflineDataStorage.get<SpicesResponse>(OfflineDataStorage.spices) != null
                    isInitSpices = false
                    Log.w(TAG, "Lấy loài online không được, tiến hành lấy offline")
                }

                override fun onResponse(
                    call: Call<SpicesResponse>,
                    response: Response<SpicesResponse>
                ) {
                    if (response.isSuccessful) {
                        val spicesResponse = response.body()
                        if (spicesResponse != null) {

//                            for (spice in spicesResponse.spices) {
//                                Log.w("DataInit", spice.name)
//                                Log.w("DataInit", spice.image)
//                            }
                            if (OfflineDataStorage.saveData(
                                    OfflineDataStorage.spices,
                                    spicesResponse
                                )
                            ) {
                                Log.w("DataInit", "Lưu dữ liệu loài thành công")
                            } else {
                                Log.w("DataInit", "Lưu dữ liệu loài KHÔNG thành công")
                            }
                            isInitSpiceSuccess = true
                            isInitSpices = false
                            return
                        }
                    }
                    // Khi lấy không thành công
                    isInitSpiceSuccess =
                        OfflineDataStorage.get<SpicesResponse>(OfflineDataStorage.spices) != null
                    isInitSpices = false
                }
            })
        }

        fun initJobs() {
            Log.w(TAG, "Tiến hành lấy dữ liệu danh sách nghề")
            APIUtils.mAPIServices?.getJobAPI()?.enqueue(object : Callback<GetJobsResponse> {
                override fun onFailure(call: Call<GetJobsResponse>, t: Throwable) {
                    isInitJobSuccess =
                        OfflineDataStorage.get<GetJobsResponse>(OfflineDataStorage.jobs) != null
                    isInitJobs = false
                    Log.w(TAG, "Lấy dữ liệu nghề online thất bại, lấy dữ liệu offline")
                }

                override fun onResponse(
                    call: Call<GetJobsResponse>,
                    response: Response<GetJobsResponse>
                ) {
                    if (response.isSuccessful) {
                        val getJobsResponse = response.body()
                        if (getJobsResponse != null) {
                            Log.w(TAG, "Tiến hành lấy dữ liệu nghề xong")
                            OfflineDataStorage.saveData(
                                OfflineDataStorage.jobs,
                                getJobsResponse
                            )
                            isInitJobSuccess = true
                            isInitJobs = false
                            return
                        }
                    }
                    Log.w(TAG, "Lấy online không được, lấy offline")
                    // Khi lấy không thành công
                    isInitJobSuccess =
                        OfflineDataStorage.get<GetJobsResponse>(OfflineDataStorage.jobs) != null
                    isInitJobs = false
                }
            })
        }
    }
}