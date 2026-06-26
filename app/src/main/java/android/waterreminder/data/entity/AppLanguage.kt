package android.waterreminder.data.entity

import androidx.annotation.StringRes
import android.waterreminder.R

enum class AppLanguage(
    val isoCode: String,
    @param:StringRes val labelRes: Int
) {
    ENGLISH("en", R.string.language_english),
    ARABIC("ar", R.string.language_arabic),
    GERMAN("de", R.string.language_german),
    FRENCH("fr", R.string.language_french),
    ITALIAN("it", R.string.language_italian);

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