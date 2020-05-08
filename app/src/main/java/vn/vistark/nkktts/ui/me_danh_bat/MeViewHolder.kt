package vn.vistark.nkktts.ui.me_danh_bat

import Hauls
import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import vn.vistark.nkktts.R
import vn.vistark.nkktts.ui.danh_sach_loai.ManHinhDanhSachLoai

class MeViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    val imdbLnBtnRoot: LinearLayout = v.findViewById(R.id.imdbLnBtnRoot)
    private val imdbTvTenMe: TextView = v.findViewById(R.id.imdbTvTenMe)
    private val imdbTvKhoangThoiGian: TextView = v.findViewById(R.id.imdbTvKhoangThoiGian)

    @SuppressLint("SetTextI18n")
    fun bind(hauls: Hauls) {
        imdbTvTenMe.text = "#${hauls.orderNumber}"
        imdbTvKhoangThoiGian.text =
            "${hauls.timeDropNets}\r\n${hauls.timeCollectingNets}"
    }
}