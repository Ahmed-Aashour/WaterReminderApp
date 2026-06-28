package android.waterreminder.data.entity

import androidx.annotation.StringRes
import android.waterreminder.R
import kotlin.math.roundToInt

enum class AppUnit(
    val key: String,
    @param:StringRes val unitRes: Int,    // (e.g., ml)
    @param:StringRes val nameRes: Int,    // (e.g., "Milliliters (ml)")
    @param:StringRes val formatRes: Int   // (e.g., "%1$d ml")
) {
    ML(
        key = "ml",
        unitRes = R.string.unit_ml,
        nameRes = R.string.unit_name_ml,
        formatRes = R.string.unit_format_ml
    ){
        override fun convertFromMl(amountMl: Int): Int = amountMl
    },
    OZ(
        key = "fl oz",
        unitRes = R.string.unit_oz,
        nameRes = R.string.unit_name_oz,
        formatRes = R.string.unit_format_oz
    ){
        // Keeps factor constants contained right where they belong
        private val ML_TO_OZ_FACTOR = 0.0338140227
        override fun convertFromMl(amountMl: Int): Int = (amountMl * ML_TO_OZ_FACTOR).roundToInt()
    };

    /**
     * Translates standard baseline milliliters into the target presentation quantity.
     */
    abstract fun convertFromMl(amountMl: Int): Int

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