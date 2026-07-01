package android.waterreminder.data.repository

import android.waterreminder.data.di.DateFormatShort
import android.waterreminder.data.di.TimeFormat24Hour
import android.waterreminder.data.entity.DayPrayerTimes
import android.waterreminder.service.remote.AladhanApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/**
 * Production implementation of the [PrayerTimesRepository] data source contract.
 *
 * Coordinates downstream server operations via [AladhanApiService], sanitizes variable
 * timezone indicators returned by the network layer, and safely materializes domain-level
 * [DayPrayerTimes] data models.
 */
class PrayerTimesRepositoryImpl @Inject constructor(
    private val apiService: AladhanApiService,
    @param:TimeFormat24Hour private val timeFormatter: DateTimeFormatter,
    @param:DateFormatShort private val dateFormatter: DateTimeFormatter,
) : PrayerTimesRepository {

    /**
     * Retrieves and maps specialized solar prayer schedules for a given location and date coordinate.
     *
     * Forces background stream containment by executing network requests over `Dispatchers.IO`.
     * Catches downstream connection dropping or serialization failures through an explicit [Result] wrapper.
     */
    override suspend fun getPrayerTimesForDate(
        date: LocalDate,
        city: String,
        country: String
    ): Result<DayPrayerTimes> = withContext(Dispatchers.IO) {
        runCatching {
            val dateString = date.format(dateFormatter)

            val response = apiService.getTimingsByCity(
                dateString = dateString,
                city = city,
                country = country,
                calculationMethod = DEFAULT_METHOD // TODO: Wire dynamic settings parameters downstream here
            )

            val timings = response.data.timings

            // Strip trailing zone metadata flags seamlessly if provided by backend streams (e.g., "19:43 (EEST)" -> "19:43")
            val cleanedFajr = timings.fajr.substringBefore(" ")
            val cleanedMaghrib = timings.maghrib.substringBefore(" ")

            DayPrayerTimes(
                fajr = LocalTime.parse(cleanedFajr, timeFormatter),
                maghrib = LocalTime.parse(cleanedMaghrib, timeFormatter)
            )
        }
    }

    companion object {
        /**
         * Fallback calculation method code token matching Egyptian General Authority parameter standards.
         */
        private const val DEFAULT_METHOD = 5
    }
}