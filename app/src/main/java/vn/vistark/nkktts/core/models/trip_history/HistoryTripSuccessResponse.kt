package vn.vistark.nkktts.core.models.trip_history

import com.google.gson.annotations.SerializedName

data class HistoryTripSuccessResponse(
    @SerializedName("status") var status: Int,
    @SerializedName("message") var message: String,
    @SerializedName("data") var data: List<HtripData>
) {
    data class HtripData(
        @SerializedName("id") var id: String,
        @SerializedName("trip_number") var trip_number: String,
        @SerializedName("departure_port") var departure_port: String,
        @SerializedName("departure_time") var departure_time: String,
        @SerializedName("destination_port") var destination_port: String,
        @SerializedName("destination_time") var destination_time: String,
        @SerializedName("submit_time") var submit_time: String,
        @SerializedName("captain_id") var captain_id: String,
        @SerializedName("item_species_trip") var item_species_trip: String,
        @SerializedName("total_weight_trip") var total_weight_trip: String,
        @SerializedName("created_at") var created_at: String,
        @SerializedName("updated_at") var updated_at: String
    )
}

//    @SerializedName("____") var __:String,