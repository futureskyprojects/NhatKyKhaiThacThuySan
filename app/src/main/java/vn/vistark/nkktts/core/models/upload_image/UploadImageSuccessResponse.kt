package vn.vistark.nkktts.core.models.upload_image

import com.google.gson.annotations.SerializedName

data class UploadImageSuccessResponse(
    @SerializedName("status") var status: Int,
    @SerializedName("message") var message: String,
    @SerializedName("result") var path: ServerImagePath
) {
    data class ServerImagePath(@SerializedName("path") var path: String)
}