package android.waterreminder.data.entity

import androidx.annotation.StringRes
import android.waterreminder.R

enum class AppLanguage(val isoCode: String) {
    ENGLISH("en"),
    ARABIC("ar"),
    GERMAN("de"),
    FRENCH("fr"),
    ITALIAN("it");

    /**
     * Returns the correct key resource ID for localization.
     */
    @StringRes
    fun getDisplayLabelRes(): Int {
        return when (this) {
            ENGLISH -> R.string.language_english
            ARABIC -> R.string.language_arabic
            GERMAN -> R.string.language_german
            FRENCH -> R.string.language_french
            ITALIAN -> R.string.language_italian
        }
    }

    companion object {
        /**
         * Safely parses a stored ISO key code back into an AppLanguage enum instance.
         * Falls back safely to ENGLISH if unrecognized or null.
         */
        fun fromIsoCode(code: String?): AppLanguage {
            return entries.find { it.isoCode == code } ?: ENGLISH
        }
    }
}