package android.waterreminder.service.usecase

import android.waterreminder.data.entity.UserPreferences
import android.waterreminder.data.repository.LocationRepository
import android.waterreminder.data.repository.PrayerTimesRepository
import android.waterreminder.data.store.AppSettingsDataStore
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

data class TrackingWindow(
    val startTime: String,
    val endTime: String
)

class ResolveTrackingWindowUseCase @Inject constructor(
    private val locationRepository: LocationRepository,
    private val prayerTimesRepository: PrayerTimesRepository
) {
    private val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.US)

    /**
     * Resolves the active tracking window based on user preferences.
     * Evaluates fasting constraints dynamically via network or cache lookups if enabled.
     */
    suspend fun execute(prefs: UserPreferences): TrackingWindow {
        if (!prefs.isFasting) {
            return TrackingWindow(
                startTime = prefs.startTime,
                endTime = prefs.endTime
            )
        }

        // Fasting state is active: Look up coordinates and request prayer schedule metrics
        val locationProfile = locationRepository.getCurrentLocationProfile()
        val targetCity = locationProfile?.city ?: "Alexandria"
        val targetCountry = locationProfile?.country ?: "Egypt"

        val todayTimes = prayerTimesRepository.getPrayerTimesForDate(LocalDate.now(), targetCity, targetCountry).getOrNull()
        val tomorrowTimes = prayerTimesRepository.getPrayerTimesForDate(LocalDate.now().plusDays(1), targetCity, targetCountry).getOrNull()

        val operationalStart = todayTimes?.maghrib?.format(timeFormatter) ?: AppSettingsDataStore.DEFAULT_FASTING_START_TIME
        val operationalEnd = tomorrowTimes?.fajr?.format(timeFormatter) ?: AppSettingsDataStore.DEFAULT_FASTING_END_TIME

        return TrackingWindow(
            startTime = operationalStart,
            endTime = operationalEnd
        )
    }
}