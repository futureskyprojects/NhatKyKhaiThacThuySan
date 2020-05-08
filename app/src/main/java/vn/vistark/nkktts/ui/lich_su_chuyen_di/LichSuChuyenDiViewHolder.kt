package vn.vistark.nkktts.ui.lich_su_chuyen_di

import android.annotation.SuppressLint
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import vn.vistark.nkktts.R
import vn.vistark.nkktts.core.models.trip_history.HistoryTripSuccessResponse

class LichSuChuyenDiViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    val lnLscdRoot: LinearLayout = v.findViewById(R.id.lnLscdRoot)
    val tvLscdNumber: TextView = v.findViewById(R.id.tvLscdNumber)
    val tvLscdTopString: TextView = v.findViewById(R.id.tvLscdTopString)
    val tvLscdStartPortName: TextView = v.findViewById(R.id.tvLscdStartPortName)
    val tvLscdEndPortName: TextView = v.findViewById(R.id.tvLscdEndPortName)

    @SuppressLint("SetTextI18n")
    fun bind(htripData: HistoryTripSuccessResponse.HtripData) {
        tvLscdNumber.text = htripData.trip_number
        tvLscdTopString.text = htripData.departure_time.substring(0, 10).replace("-", "/") + " - " +
                htripData.destination_time.substring(0, 10)
                    .replace(
                        "-",
                        "/"
                    ) + "\n(" + tvLscdNumber.context.getString(
            R.string.gom_x_loai_va_y_kg,
            GsonBuilder().create().toJson(htripData.item_species_trip).length,
            htripData.total_weight_trip
        ) + ")"
        tvLscdStartPortName.text = htripData.departure_port
        tvLscdEndPortName.text = htripData.destination_port

    }
}