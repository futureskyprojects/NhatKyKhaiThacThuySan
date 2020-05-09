package vn.vistark.nkktts.utils

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import vn.vistark.nkktts.core.constants.OfflineDataStorage
import java.util.*

class LanguageChange {
    companion object {
        fun onChange(baseContext: Context) {
            val s = OfflineDataStorage.get<String>("lang_code")
            var locale: Locale = Locale("vi", "VN")
            if (s != null) {
                if (s == "vi") {
                    locale = Locale("vi", "VN")
                } else {
                    locale = Locale("en", "US")
                }
            }
            Locale.setDefault(locale)
            val config: Configuration = baseContext.resources.configuration
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                config.setLocale(locale)
            } else {
                config.locale = locale
            }
            baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
        }
    }
}