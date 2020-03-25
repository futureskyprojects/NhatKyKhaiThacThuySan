import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import vn.vistark.nkktts.core.constants.Constants

/*
Copyright (c) 2020 Kotlin Data Classes Generated from JSON powered by http://www.json2kotlin.com

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

For support, please feel free to contact me at https://www.linkedin.com/in/syedabsar */


data class Hauls(

    @SerializedName("order_number") var orderNumber: Int = -1,
    @SerializedName("time_drop_nets") var timeDropNets: String = "",
    @SerializedName("lat_drop") var latDrop: String = "",
    @SerializedName("lng_drop") var lngDrop: String = "",
    @SerializedName("time_collecting_nets") var timeCollectingNets: String = "",
    @SerializedName("lat_collecting") var latCollecting: String = "",
    @SerializedName("lng_collecting") var lngCollecting: String = "",
    @SerializedName("spices") var spices: List<CatchedSpices> = emptyList()
) {
    fun update(hauls: Hauls) {
        this.orderNumber = hauls.orderNumber
        this.timeDropNets = hauls.timeDropNets
        this.latDrop = hauls.latDrop
        this.lngDrop = hauls.lngDrop
        this.timeCollectingNets = hauls.timeCollectingNets
        this.latCollecting = hauls.latCollecting
        this.lngCollecting = hauls.lngCollecting
        this.spices = hauls.spices
    }

    companion object {
        var currentHault = Hauls()
        fun updateHault() {
            if (Constants.currentTrip.trip.hauls.isNotEmpty()) {
                Log.w("HAULS", "Không trống")
                // Nếu hauls không rỗng và đã tồn tại, tiến hành cập nhật
                for (i in Constants.currentTrip.trip.hauls.indices) {
                    if (Constants.currentTrip.trip.hauls[i].orderNumber == currentHault.orderNumber) {
                        Constants.currentTrip.trip.hauls[i].update(currentHault)
                        return
                    }
                }
            }
            Log.w("HAULS", "TRỐNG")
            // Nếu hauls rỗng hoặc chưa tồn tại haul này thì thêm mới vào
            Log.w("HAULS", GsonBuilder().create().toJson(currentHault))
            val temp = ArrayList(Constants.currentTrip.trip.hauls)
            temp.add(currentHault)
            Constants.currentTrip.trip.hauls = temp.toList()
            Log.w("HAULS", GsonBuilder().create().toJson(Constants.currentTrip.trip.hauls))
            Constants.updateCurrentTrip()

        }
    }
}