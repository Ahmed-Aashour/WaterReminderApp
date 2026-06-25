package android.waterreminder.data.entity

import androidx.annotation.StringRes
import android.waterreminder.R

enum class AppUnit(val key: String) {
    ML("ml"),
    OZ("fl oz");

    /**
     * Returns the correct string resource ID for localization.
     */
    @StringRes
    fun getDisplayLabelRes(): Int {
        return when (this) {
            ML -> R.string.unit_ml
            OZ -> R.string.unit_oz
        }
    }

    companion object {
        /**
         * Safely parses a stored string key back into an AppUnit enum instance.
         * Falls back safely to ML if unrecognized or null.
         */
        fun fromKey(key: String?): AppUnit {
            return entries.find { it.key == key } ?: ML
        }
    }
}