package vn.vistark.nkktts.ui.danh_sach_loai

import Spices
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.vistark.nkktts.R

class SpiceAdapter(val spices: List<Spices>) : RecyclerView.Adapter<SpiceViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpiceViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_loai, parent, false)
        return SpiceViewHolder(v)
    }

    override fun getItemCount(): Int {
        return spices.size
    }

    override fun onBindViewHolder(holder: SpiceViewHolder, position: Int) {
        val spice = spices[position]
        holder.bind(spice)
    }

}