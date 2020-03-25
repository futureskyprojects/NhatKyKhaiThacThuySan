package vn.vistark.nkktts.ui.chon_cang

import SeaPorts
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.vistark.nkktts.R

class CangAdapter(val seaPorts: List<SeaPorts>) : RecyclerView.Adapter<CangViewHolder>() {
    var onPortClick: ((SeaPorts) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CangViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_cang_bien, parent, false)
        return CangViewHolder(v)
    }

    override fun getItemCount(): Int {
        return seaPorts.size
    }

    override fun onBindViewHolder(holder: CangViewHolder, position: Int) {
        val seaPort = seaPorts[position]
        holder.bind(seaPort)
        holder.icbLnBtnChonCang.setOnClickListener {
            onPortClick?.invoke(seaPort)
        }
    }

}