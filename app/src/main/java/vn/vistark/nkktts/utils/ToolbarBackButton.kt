package vn.vistark.nkktts.utils

import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import vn.vistark.nkktts.R

class ToolbarBackButton(var act: AppCompatActivity) {
    fun show() {
        act.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        act.supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    fun overrideAnimationOnEnterAndExitActivity() {
        act.overridePendingTransition(R.anim.enter, R.anim.exit)
    }

    fun overrideAnimationOnEnterAndExitActivityReveret() {
        act.overridePendingTransition(R.anim.animation_enter,
            R.anim.animation_leave)
    }

    fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.mnLichSuChuyenDi -> {
                FastStartActivity.khoiDongNhanhManHinhLichSuChuyenDi(act)
                return true
            }
            R.id.mnCaiDat -> {
                FastStartActivity.khoiDongNhanhManHinhThietLap(act)
                return true
            }
            R.id.mnCaiDat2 -> {
                FastStartActivity.khoiDongNhanhManHinhThietLap(act)
                return true
            }
            else -> {
                return false
            }
        }
    }
}