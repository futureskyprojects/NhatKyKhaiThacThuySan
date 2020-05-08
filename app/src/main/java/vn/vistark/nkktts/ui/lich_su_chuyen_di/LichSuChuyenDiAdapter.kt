package vn.vistark.nkktts.ui.lich_su_chuyen_di

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.vistark.nkktts.R
import vn.vistark.nkktts.core.constants.OfflineDataStorage

import vn.vistark.nkktts.core.models.trip_history.HistoryTripSuccessResponse

class LichSuChuyenDiAdapter : RecyclerView.Adapter<LichSuChuyenDiViewHolder>() {
    var htrips: Array<HistoryTripSuccessResponse.HtripData> =
        OfflineDataStorage.get<Array<HistoryTripSuccessResponse.HtripData>>(OfflineDataStorage.tripHistory)
            ?: emptyArray()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LichSuChuyenDiViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_lich_su, parent, false)
        return LichSuChuyenDiViewHolder(v)
    }

    override fun getItemCount(): Int {
        return htrips.size
    }

    override fun onBindViewHolder(holder: LichSuChuyenDiViewHolder, position: Int) {
        val htrip = htrips[position]
        holder.bind(htrip)
    }

}