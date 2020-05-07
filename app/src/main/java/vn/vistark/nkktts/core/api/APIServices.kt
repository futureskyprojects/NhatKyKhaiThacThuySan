package vn.vistark.nkktts.core.api

import ChangePassSuccessResponse
import CheckUser
import ForgotPasswordResponse
import GetJobsResponse
import GetSelectedJobResponse
import LoginResponse
import PreviousTripNumberReponse
import ProfileResponse
import RegisterSuccess
import SeaPortsReponse
import SpicesResponse
import SyncSuccess
import TheTripStorage
import UpdateProfileResponse
import UpdateSelectedJobResponse
import UserInfo
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*
import vn.vistark.nkktts.core.models.selected_job.SelectedJob
import vn.vistark.nkktts.core.models.upload_image.UploadImageSuccessResponse

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
    fun loginAPI(
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    //=============== Profile API ==========================//
    @GET("/api/captains/profile")
    fun profileAPI(): Call<ProfileResponse>

    @PUT("/api/captains/profile")
    fun profileUpdateAPI(@Body userInfo: UserInfo): Call<UpdateProfileResponse>

    //=============== Update Selected Job API ========================//
    @POST("/api/captains/job")
    fun updateSelectedJobAPI(@Body selectedJob: SelectedJob): Call<UpdateSelectedJobResponse>

    @GET("/api/captains/job_captain")
    fun getSelectedJobAPI(): Call<GetSelectedJobResponse>

    @POST("/api/captains/changejob")
    @FormUrlEncoded
    fun changeSelectedJobAPI(
        @Field("job_id") job_id: String,
        @Field("info_job") info_job: String
    ): Call<UpdateSelectedJobResponse>

    //=============== Get Spices API =================================//
    @GET("/api/requests/species")
    fun getSpices(): Call<SpicesResponse>

    //============== GET previous trip number ==========================//
    @GET("/api/requests/trip_number")
    fun getPreviousTripNumber(): Call<PreviousTripNumberReponse>

    @POST("/api/requests/add")
    fun syncTrip(@Body theTripStorage: TheTripStorage): Call<SyncSuccess>

    //============= Password API ======================================//
    @POST("/api/captains/checkforgotpass")
    @FormUrlEncoded
    fun checkForgotPass(
        @Field("username") username: String,
        @Field("fishing_license") fishingLicense: String
    ): Call<ForgotPasswordResponse>

    @POST("/api/captains/forgotpass")
    @FormUrlEncoded
    fun changePassword(@Field("password") password: String): Call<ChangePassSuccessResponse>

    @POST("/api/requests/upload/image")
    @Multipart
    fun uploadImage(@Part image: MultipartBody.Part): Call<UploadImageSuccessResponse>
}