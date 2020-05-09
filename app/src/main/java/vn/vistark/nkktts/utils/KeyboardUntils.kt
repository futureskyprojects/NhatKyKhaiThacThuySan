package vn.vistark.nkktts.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService


class KeyboardUntils {
    companion object {
        fun hide(context: AppCompatActivity) {
//            val view: View? = context.currentFocus
//            if (view != null) {
//
//            }
            val imm: InputMethodManager? =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm?.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        }
    }
}