package android.waterreminder.data.entity

import android.waterreminder.R
import androidx.annotation.StringRes

enum class AppTheme(val string: String) {
    LIGHT("Light"),
    DARK("Dark"),
    SYSTEM("System");

    /**
     * Returns the correct string resource ID for localization.
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
         * Safely parses a stored string back into an AppTheme enum instance.
         * Falls back safely to SYSTEM if the string is null or unrecognized.
         */
        fun fromString(string: String?): AppTheme {
            return entries.find { it.string == string } ?: SYSTEM
        }
    }
}