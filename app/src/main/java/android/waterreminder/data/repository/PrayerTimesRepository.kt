package android.waterreminder.data.repository

import android.waterreminder.data.entity.DayPrayerTimes
import java.time.LocalDate

interface PrayerTimesRepository {
    suspend fun getPrayerTimesForDate(date: LocalDate, city: String, country: String): Result<DayPrayerTimes>
}