package android.waterreminder.data.entity

import android.waterreminder.R
import androidx.annotation.StringRes

enum class AppTheme(val key: String) {
    LIGHT("Light"),
    DARK("Dark"),
    SYSTEM("System");

    /**
     * Returns the correct key resource ID for localization.
     */
    @StringRes
    fun getDisplayLabelRes(): Int {
        return when (this) {
            LIGHT -> R.string.theme_light
            DARK -> R.string.theme_dark
            SYSTEM -> R.string.theme_system
        }
    }

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