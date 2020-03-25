package vn.vistark.nkktts.ui.chon_cang

import SeaPorts
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import vn.vistark.nkktts.R

class CangViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    val icbIvLeadingIcon: ImageView = v.findViewById(R.id.icbIvLeadingIcon)
    val icbTvTenCang: TextView = v.findViewById(R.id.icbTvTenCang)
    val icbLnBtnChonCang: LinearLayout = v.findViewById(R.id.icbLnBtnChonCang)

    fun bind(seaPorts: SeaPorts) {
        icbTvTenCang.text = seaPorts.name
    }
}