package vn.vistark.nkktts.core.models.trip_history

import TheTripStorage
import Trip
import com.google.gson.annotations.SerializedName
import vn.vistark.nkktts.core.constants.OfflineDataStorage

data class TripHistory(
    @SerializedName("trip_history")
    var tripHistory: List<TheTripStorage> = emptyList()
) {
    companion object {
        var currentTripHistory = getHistory()
        fun getHistory(): TripHistory {
            val tripHistory = OfflineDataStorage.get<TripHistory>(OfflineDataStorage.tripHistory)
            if (tripHistory != null) {
                return tripHistory
            } else {
                return TripHistory()
            }
        }

        fun add(theTripHistory: TheTripStorage): Boolean {
            val temp = ArrayList(currentTripHistory.tripHistory)
            temp.add(theTripHistory)
            currentTripHistory.tripHistory = temp.toList()
            return true
        }
    }
}