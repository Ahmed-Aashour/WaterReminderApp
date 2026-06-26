package android.waterreminder.service.usecase

import android.waterreminder.data.entity.UserPreferences
import android.waterreminder.data.repository.LocationRepository
import android.waterreminder.data.repository.PrayerTimesRepository
import android.waterreminder.data.store.AppSettingsDataStore
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

class ResolveTrackingWindowUseCase @Inject constructor(
    private val locationRepository: LocationRepository,
    private val prayerTimesRepository: PrayerTimesRepository,
) {
    /**
     * Resolves the active tracking window based on user preferences.
     * Evaluates fasting constraints dynamically via network or cache lookups if enabled.
     * * @return A [Pair] where the first element is the startTime and the second is the endTime.
     */
    suspend fun execute(prefs: UserPreferences): Pair<LocalTime, LocalTime> {
        if (!prefs.isFasting) {
            return Pair(prefs.startTime, prefs.endTime)
        }

        // Fetching the location
        val locationProfile = locationRepository.getCurrentLocationProfile()

        val targetCity = locationProfile?.city?.takeIf { it.isNotEmpty() }
            ?: prefs.city.takeIf { it.isNotEmpty() }

        val targetCountry = locationProfile?.country?.takeIf { it.isNotEmpty() }
            ?: prefs.country.takeIf { it.isNotEmpty() }

        if (targetCity == null || targetCountry == null) {
            return Pair(
                AppSettingsDataStore.DEFAULT_FASTING_START_TIME,
                AppSettingsDataStore.DEFAULT_FASTING_END_TIME
            )
        }

        // Fetching the start and end times by the location
        val todayTimes = prayerTimesRepository.getPrayerTimesForDate(
            LocalDate.now(), targetCity, targetCountry
        ).getOrNull()

        val tomorrowTimes = prayerTimesRepository.getPrayerTimesForDate(
            LocalDate.now().plusDays(1), targetCity, targetCountry
        ).getOrNull()

        // 👈 Directly using LocalTime references instead of formatting to 12-hour strings
        val operationalStart = todayTimes?.maghrib ?: AppSettingsDataStore.DEFAULT_FASTING_START_TIME
        val operationalEnd = tomorrowTimes?.fajr ?: AppSettingsDataStore.DEFAULT_FASTING_END_TIME

        return Pair(operationalStart, operationalEnd)
    }
}