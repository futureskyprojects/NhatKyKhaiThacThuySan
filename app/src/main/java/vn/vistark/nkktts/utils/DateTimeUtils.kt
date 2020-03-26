package vn.vistark.nkktts.utils

import java.util.*

class DateTimeUtils {
    companion object {
        fun getStringCurrentYMD(): String {
            val calendar = Calendar.getInstance()
            return String.format(
                "%04d-%02d-%02d",
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH)
            )
        }

        fun getStringCurrentYMDHMS(): String {
            val calendar = Calendar.getInstance()
            return String.format(
                "%04d-%02d-%02d %02d:%02d:%02d",
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                calendar.get(Calendar.SECOND)
            )
        }
    }
}