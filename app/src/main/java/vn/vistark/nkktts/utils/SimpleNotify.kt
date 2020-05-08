package vn.vistark.nkktts.utils

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import vn.vistark.nkktts.R

class SimpleNotify {
    companion object {
        fun error(context: Context, title: String, msg: String, canClose: Boolean = true) {
            val s = SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(title)
                .setContentText(msg)
            s.setCancelable(canClose)
            s.show()
        }

        fun success(context: Context, title: String, msg: String) {
            SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(title)
                .setContentText(msg)
                .show()
        }

        fun warning(context: Context, title: String, msg: String) {
            SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(title)
                .setContentText(msg)
                .show()
        }

        fun onBackConfirm(context: AppCompatActivity, func: () -> Unit) {
            SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(context.getString(R.string.tiep_tuc).toUpperCase() + "?")
                .setContentText("")
                .setConfirmButton(context.getString(R.string.dong_y)) {
                    it.dismiss()
                    func()
                }
                .setCancelButton(context.getString(R.string.huy)) {
                    it.dismiss()
                }
                .show()
        }
    }
}