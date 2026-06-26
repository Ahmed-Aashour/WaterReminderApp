package android.waterreminder.data.entity

import java.time.LocalTime

data class DayPrayerTimes(
    val fajr: LocalTime,
    val maghrib: LocalTime
)