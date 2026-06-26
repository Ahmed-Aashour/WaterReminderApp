package android.waterreminder.data.entity

import android.waterreminder.R
import androidx.annotation.StringRes

enum class AppTheme(
    val key: String,
    @param:StringRes val labelRes: Int
) {
    LIGHT("Light", R.string.theme_light),
    DARK("Dark", R.string.theme_dark),
    SYSTEM("System", R.string.theme_system);

    companion object {
        /**
         * Safely parses a stored key back into an AppTheme enum instance.
         * Falls back safely to SYSTEM if the key is null or unrecognized.
         */
        fun fromKey(key: String?): AppTheme {
            return entries.find { it.key == key } ?: SYSTEM
        }
    }
}