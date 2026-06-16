package android.waterreminder.data.repository

import android.waterreminder.data.entity.DayPrayerTimes
import android.waterreminder.service.remote.AladhanApiService
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class PrayerTimesRepositoryImpl @Inject constructor(
    private val apiService: AladhanApiService
) : PrayerTimesRepository {

    private val apiTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    private val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

    override suspend fun getPrayerTimesForDate(
        date: LocalDate,
        city: String,
        country: String
    ): Result<DayPrayerTimes> = runCatching {
        val dateString = date.format(dateFormatter)
        // API Example: https://api.aladhan.com/v1/timingsByCity/16-06-2026?city=Cairo&country=Egypt&method=5
        val response = apiService.getTimingsByCity(
            dateString = dateString,
            city = city,
            country = country,
            calculationMethod = 5
        )

        val timings = response.data.timings

        // Strip out trailing zone strings like " (EEST)" if present in response
        val cleanedFajr = timings.fajr.substringBefore(" ")
        val cleanedMaghrib = timings.maghrib.substringBefore(" ")

        DayPrayerTimes(
            fajr = LocalTime.parse(cleanedFajr, apiTimeFormatter),
            maghrib = LocalTime.parse(cleanedMaghrib, apiTimeFormatter)
        )
    }
}