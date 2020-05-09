package vn.vistark.nkktts.utils

import com.google.android.gms.common.util.IOUtils.toByteArray
import java.math.BigInteger
import java.security.MessageDigest

class CryptoUtils {
    companion object {
        fun md5Hash(s: String): String {
            val md = MessageDigest.getInstance("MD5")
            return BigInteger(1, md.digest(s.toByteArray())).toString(16).padStart(32, '0')
        }
    }
}