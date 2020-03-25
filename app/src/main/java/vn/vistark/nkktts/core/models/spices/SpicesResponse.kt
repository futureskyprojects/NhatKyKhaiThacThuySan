import com.google.gson.annotations.SerializedName

data class SpicesResponse(
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("data") val spices: List<Spices>
)