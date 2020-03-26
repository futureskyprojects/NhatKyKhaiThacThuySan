package vn.vistark.nkktts.core.api

import CheckUser
import GetJobsResponse
import LoginResponse
import PreviousTripNumberReponse
import ProfileResponse
import RegisterSuccess
import SeaPortsReponse
import SpicesResponse
import SyncSuccess
import TheTripStorage
import UpdateSelectedJobResponse
import UserInfo
import retrofit2.Call
import retrofit2.http.*
import vn.vistark.nkktts.core.models.selected_job.SelectedJob

public interface APIServices {
    //=============== Check User API ========================//
    @POST("/api/captains/checkuser")
    @FormUrlEncoded
    fun checkUserAPI(@Field("username") username: String): Call<CheckUser>

    //=============== Captain Register API ==================//
    @POST("/api/captains/register")
    fun registerAPI(@Body userInfo: UserInfo): Call<RegisterSuccess>

    //=============== Get job API ==========================//
    @GET("/api/captains/getJob")
    fun getJobAPI(): Call<GetJobsResponse>

    //=============== Get Sea Ports API ====================//
    @GET("/api/requests/ports")
    fun getSeaPorts(): Call<SeaPortsReponse>

    //=============== Login API ============================//
    @POST("/api/captains/login")
    @FormUrlEncoded
    fun loginAPI(@Field("username") username: String, @Field("password") password: String): Call<LoginResponse>

    //=============== Profile API ==========================//
    @GET("/api/captains/profile")
    fun profileAPI(): Call<ProfileResponse>

    //=============== Update Selected Job API ========================//
    @POST("/api/captains/job")
    fun updateSelectedJobAPI(@Body selectedJob: SelectedJob): Call<UpdateSelectedJobResponse>

    //=============== Get Spices API =================================//
    @GET("/api/requests/species")
    fun getSpices(): Call<SpicesResponse>

    //============== GET previous trip number ==========================//
    @GET("/api/requests/trip_number")
    fun getPreviousTripNumber(): Call<PreviousTripNumberReponse>

    @POST("/api/requests/add")
    fun syncTrip(@Body theTripStorage: TheTripStorage): Call<SyncSuccess>
}