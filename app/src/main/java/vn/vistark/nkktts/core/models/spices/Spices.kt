import com.google.gson.annotations.SerializedName

data class Spices(

    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String?,
    @SerializedName("type_job") val typeJob: Int?,
    @SerializedName("image") val image: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)