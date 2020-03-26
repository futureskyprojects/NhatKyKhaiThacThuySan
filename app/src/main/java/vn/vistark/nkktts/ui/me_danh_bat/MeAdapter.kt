package vn.vistark.nkktts.ui.me_danh_bat

import Hauls
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.vistark.nkktts.R
import vn.vistark.nkktts.core.constants.Constants

class MeAdapter() : RecyclerView.Adapter<MeViewHolder>() {

    var onHaulClick: ((Hauls) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.item_me_danh_bat, parent, false)
        return MeViewHolder(v)
    }

    override fun getItemCount(): Int {
        return Constants.currentTrip.trip.hauls.size
    }

    override fun onBindViewHolder(holder: MeViewHolder, position: Int) {
        val hauls = Constants.currentTrip.trip.hauls[position]
        holder.bind(hauls)
        holder.imdbLnBtnRoot.setOnClickListener {
            onHaulClick?.invoke(hauls)
        }
    }

}