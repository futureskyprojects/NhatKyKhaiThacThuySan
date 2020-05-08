package vn.vistark.nkktts.ui.lich_su_chuyen_di

import CatchedSpices
import SeaPortsReponse
import TheTripStorage
import android.annotation.SuppressLint
import android.graphics.Color
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import vn.vistark.nkktts.R
import vn.vistark.nkktts.core.constants.OfflineDataStorage
import vn.vistark.nkktts.core.db.TripWaitForSync
import vn.vistark.nkktts.core.models.trip_history.HistoryTripSuccessResponse

class LichSuChuyenDiChuaDongBoViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    val lnLscdRoot: LinearLayout = v.findViewById(R.id.lnLscdRoot)
    val tvLscdNumber: TextView = v.findViewById(R.id.tvLscdNumber)
    val tvLscdTopString: TextView = v.findViewById(R.id.tvLscdTopString)
    val tvLscdStartPortName: TextView = v.findViewById(R.id.tvLscdStartPortName)
    val tvLscdEndPortName: TextView = v.findViewById(R.id.tvLscdEndPortName)
    val btnLsXoa: Button = v.findViewById(R.id.btnLsXoa)

    val seaPorts = OfflineDataStorage.get<SeaPortsReponse>(OfflineDataStorage.seaPorts)?.seaPorts

    @SuppressLint("SetTextI18n")
    fun bind(ts: TripWaitForSync.TripInDb, onDelete: () -> Unit) {
        if (ts.trip.trip.isSynced) {
            btnLsXoa.visibility = View.VISIBLE
            btnLsXoa.setOnClickListener {
                SweetAlertDialog(btnLsXoa.context, SweetAlertDialog.WARNING_TYPE).apply {
                    titleText = btnLsXoa.context.getString(R.string.ban_co_chac_chan)
                    setConfirmButton(btnLsXoa.context.getString(R.string.dong_y)) {
                        it.dismiss()
                        if (TripWaitForSync(btnLsXoa.context).remove(ts.id) != -1) {
                            Toast.makeText(
                                btnLsXoa.context,
                                btnLsXoa.context.getString(R.string.da_xoa),
                                Toast.LENGTH_SHORT
                            ).show()
                            onDelete()
                        }
                    }
                    setCancelButton(btnLsXoa.context.getString(R.string.huy)) {
                        it.dismiss()
                    }
                    show()
                }
            }
            lnLscdRoot.setBackgroundColor(Color.parseColor("#5FFF5722"))
        } else {
            lnLscdRoot.setBackgroundColor(Color.parseColor("#7C7C8EF4"))
        }

        var arrSpices: Array<CatchedSpices> = emptyArray()
        var sumWeight = 0F
        for (haul in ts.trip.trip.hauls) {
            for (spice in haul.spices) {
                sumWeight += spice.weight
                if (!arrSpices.contains(spice)) {
                    arrSpices = arrSpices.plus(spice)
                }
            }
        }

        tvLscdNumber.text = ts.trip.trip.tripNumber.toString()
        tvLscdTopString.text = ts.trip.trip.departureTime
            .substring(0, 10).replace("-", "/") + " - " +
                ts.trip.trip.destinationTime.substring(0, 10)
                    .replace(
                        "-",
                        "/"
                    ) + "\n(" + tvLscdNumber.context.getString(
            R.string.gom_x_loai_va_y_kg,
            GsonBuilder().create().toJson(arrSpices.size).length,
            sumWeight.toString()
        ) + ") [" + (if (ts.trip.trip.isSynced) tvLscdNumber.context.getString(R.string.bi_trung) else tvLscdNumber.context.getString(
            R.string.chua_dong_bo
        )) + "]"
        if (seaPorts == null) {
            tvLscdStartPortName.text = tvLscdStartPortName.context.getText(R.string.tag_khong_ro)
            tvLscdEndPortName.text = tvLscdStartPortName.context.getText(R.string.tag_khong_ro)
        } else {
            tvLscdStartPortName.text = seaPorts.find { it.id == ts.trip.trip.departurePort }?.name
                ?: tvLscdStartPortName.context.getText(R.string.tag_khong_ro)
            tvLscdEndPortName.text = seaPorts.find { it.id == ts.trip.trip.destinationPort }?.name
                ?: tvLscdStartPortName.context.getText(R.string.tag_khong_ro)
        }
    }
}