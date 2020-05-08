package vn.vistark.nkktts.ui.lich_su_chuyen_di

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.vistark.nkktts.R
import vn.vistark.nkktts.core.db.TripWaitForSync

class LichSuChuyenDiChuaDongBoAdapter(var tripSs: Array<TripWaitForSync.TripInDb>) :
    RecyclerView.Adapter<LichSuChuyenDiChuaDongBoViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LichSuChuyenDiChuaDongBoViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_lich_su, parent, false)
        return LichSuChuyenDiChuaDongBoViewHolder(v)
    }

    override fun getItemCount(): Int {
        return tripSs.size
    }

    override fun onBindViewHolder(holder: LichSuChuyenDiChuaDongBoViewHolder, position: Int) {
        val htrip = tripSs[position]
        holder.bind(htrip) {
            tripSs = tripSs.filter { it.id != htrip.id }.toTypedArray()
            notifyDataSetChanged()
        }
    }

}