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
        var isInitSeaPort = true
        var isInitSeaPortSuccess = false

        var isInitSpices = true
        var isInitSpiceSuccess = false

        var isInitJobs = true
        var isInitJobSuccess = false

        fun isFinished(): Boolean {
            return (!isInitSeaPort && !isInitSpices && !isInitJobs)
        }

        fun initSeaPorts() {
            APIUtils.mAPIServices?.getSeaPorts()?.enqueue(object : Callback<SeaPortsReponse> {
                override fun onFailure(call: Call<SeaPortsReponse>, t: Throwable) {
                    isInitSeaPortSuccess =
                        OfflineDataStorage.get<SeaPortsReponse>(OfflineDataStorage.seaPorts) != null
                    isInitSeaPort = false
                }

                override fun onResponse(
                    call: Call<SeaPortsReponse>,
                    response: Response<SeaPortsReponse>
                ) {
                    if (response.isSuccessful) {
                        val seaPortsReponse = response.body()
                        if (seaPortsReponse != null) {
                            OfflineDataStorage.saveData(
                                OfflineDataStorage.seaPorts,
                                seaPortsReponse
                            )
                            isInitSeaPortSuccess = true
                            isInitSeaPort = false
                            return
                        }
                    }
                    // Khi lấy không thành công
                    isInitSeaPortSuccess =
                        OfflineDataStorage.get<SeaPortsReponse>(OfflineDataStorage.seaPorts) != null
                    isInitSeaPort = false
                }
            })
        }

        fun initSpices() {
            APIUtils.mAPIServices?.getSpices()?.enqueue(object : Callback<SpicesResponse> {
                override fun onFailure(call: Call<SpicesResponse>, t: Throwable) {
                    isInitSpiceSuccess =
                        OfflineDataStorage.get<SpicesResponse>(OfflineDataStorage.spices) != null
                    isInitSpices = false
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
                                Log.w("DataInit", "Lưu dữ liệu thành công")
                            } else {
                                Log.w("DataInit", "Lưu dữ liệu KHÔNG thành công")
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
            APIUtils.mAPIServices?.getJobAPI()?.enqueue(object : Callback<GetJobsResponse> {
                override fun onFailure(call: Call<GetJobsResponse>, t: Throwable) {
                    isInitJobSuccess =
                        OfflineDataStorage.get<GetJobsResponse>(OfflineDataStorage.jobs) != null
                    isInitJobs = false
                }

                override fun onResponse(
                    call: Call<GetJobsResponse>,
                    response: Response<GetJobsResponse>
                ) {
                    if (response.isSuccessful) {
                        val getJobsResponse = response.body()
                        if (getJobsResponse != null) {
                            OfflineDataStorage.saveData(
                                OfflineDataStorage.jobs,
                                getJobsResponse
                            )
                            isInitJobSuccess = true
                            isInitJobs = false
                            return
                        }
                    }
                    // Khi lấy không thành công
                    isInitJobSuccess =
                        OfflineDataStorage.get<GetJobsResponse>(OfflineDataStorage.jobs) != null
                    isInitJobs = false
                }
            })
        }
    }
}