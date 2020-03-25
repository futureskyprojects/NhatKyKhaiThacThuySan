package vn.vistark.nkktts.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import java.lang.Exception


class Base64ToBitmap {
    companion object {
        fun process(base64Str: String): Bitmap? {
            try {
                val decodedString: ByteArray = Base64.decode(base64Str, Base64.DEFAULT)
                val decodedByte =
                    BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                return decodedByte
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }
    }
}