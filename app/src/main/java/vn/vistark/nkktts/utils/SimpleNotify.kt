package vn.vistark.nkktts.utils

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog

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
                .setTitleText("TIẾP TỤC?")
                .setContentText("")
                .setConfirmButton("Đồng ý") {
                    it.dismiss()
                    func()
                }
                .setCancelButton("Hủy") {
                    it.dismiss()
                }
                .show()
        }
    }
}