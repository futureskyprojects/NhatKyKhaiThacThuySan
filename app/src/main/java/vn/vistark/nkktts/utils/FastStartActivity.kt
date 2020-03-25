package vn.vistark.nkktts.utils

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import vn.vistark.nkktts.ui.thiet_lap.ManHinhThietLap

class FastStartActivity {
    companion object {
        fun khoiDongNhanhManHinhThietLap(act: AppCompatActivity) {
            val manHinhThietLapIntent = Intent(act, ManHinhThietLap::class.java)
            act.startActivity(manHinhThietLapIntent)
        }

        fun khoiDongNhanhManHinhLichSuChuyenDi(act: AppCompatActivity) {

        }
    }
}