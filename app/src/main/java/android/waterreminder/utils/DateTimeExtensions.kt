package android.waterreminder.utils

import java.time.LocalDate
import java.time.ZoneId

/**
 * Computes the exact Unix Epoch millisecond timestamp representing 00:00:00 AM
 * of the current calendar date under the device's active local time zone profile.
 */
val LocalDate.startOfDayEpochMillis: Long
    get() = this.atStartOfDay(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()