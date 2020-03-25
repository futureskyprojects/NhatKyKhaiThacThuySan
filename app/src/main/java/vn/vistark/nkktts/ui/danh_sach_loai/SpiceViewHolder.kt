package vn.vistark.nkktts.ui.danh_sach_loai

import Spices
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import vn.vistark.nkktts.R
import vn.vistark.nkktts.utils.Base64ToBitmap

class SpiceViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    val ilLnLoai: LinearLayout = v.findViewById(R.id.ilLnLoai)
    val ilTvSanLuong: TextView = v.findViewById(R.id.ilTvSanLuong)
    val ilTvTenLoai: TextView = v.findViewById(R.id.ilTvTenLoai)
    val ilIvLoai: ImageView = v.findViewById(R.id.ilIvLoai)

    fun bind(spices: Spices) {
        ilTvTenLoai.text = spices.name
        if (spices.image != null && spices.image.isNotEmpty()) {
            val bm = Base64ToBitmap.process(spices.image)
            if (bm != null) {
                ilIvLoai.setImageBitmap(bm)
            }
        }

        if (Hauls.currentHault.spices.isNotEmpty()) {
            for (spice in Hauls.currentHault.spices) {
                if (spice.id == spices.id) {
                    ilTvSanLuong.text = spice.weight.toString()
                }
            }
        }
    }
}