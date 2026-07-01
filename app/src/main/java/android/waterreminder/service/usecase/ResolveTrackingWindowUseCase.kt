package android.waterreminder.service.usecase

import android.util.Log
import android.waterreminder.data.entity.UserPreferences
import android.waterreminder.data.repository.LocationRepository
import android.waterreminder.data.repository.PrayerTimesRepository
import android.waterreminder.data.store.AppSettingsDataStore
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

/**
 * Business logic contract responsible for resolving active hydration tracking windows.
 *
 * This use case acts as the central coordinator for parsing active operational boundaries.
 * It determines whether the application should track water intake across standard user configurations
 * or calculate tracking boundaries based on intermittent solar fasting patterns (Fajr and Maghrib times).
 */
class ResolveTrackingWindowUseCase @Inject constructor(
    private val locationRepository: LocationRepository,
    private val prayerTimesRepository: PrayerTimesRepository,
) {
    /**
     * Resolves the start and end [LocalTime] boundaries for fluid consumption based on active preferences.
     *
     * To protect the device from battery drain, this function leverages a cached profile strategy:
     * it prioritizes the city and country strings stored inside [UserPreferences] and only falls back to
     * a live GPS location lookup if those persistent strings are completely blank.
     *
     * @param prefs The immutable configuration preferences snapshot derived from the app data store.
     * @return A [Pair] containing the calculated operational start time (first) and end time (second).
     */
    suspend fun execute(prefs: UserPreferences): Pair<LocalTime, LocalTime> {
        // CASE 1: Fasting metrics are disabled -> Return standard baseline user constraints immediately
        if (!prefs.isFasting) {
            return Pair(prefs.startTime, prefs.endTime)
        }

        // Prioritize stored preferences over fresh GPS coordinate calls to save battery life.
        var targetCity = prefs.city.trim()
        var targetCountry = prefs.country.trim()

        // Fallback to high-overhead GPS check ONLY if saved values are entirely missing
        if (targetCity.isBlank() || targetCountry.isBlank()) {
            Log.d(TAG, "Cached location profile blank. Requesting active device GPS fallback telemetry...")
            val locationProfile = locationRepository.getCurrentLocationProfile()

            if (locationProfile != null) {
                targetCity = locationProfile.city.trim()
                targetCountry = locationProfile.country.trim()
            }
        }

        // If location profile resolution remains unresolvable, fall back to global default thresholds
        if (targetCity.isBlank() || targetCountry.isBlank()) {
            Log.w(TAG, "Location metrics could not be resolved. Appending global system defaults.")
            return Pair(
                AppSettingsDataStore.DEFAULT_FASTING_START_TIME,
                AppSettingsDataStore.DEFAULT_FASTING_END_TIME
            )
        }

        val today = LocalDate.now()

        // Execute background solar scheduling fetches
        val todayTimes = prayerTimesRepository.getPrayerTimesForDate(today, targetCity, targetCountry).getOrNull()
        val tomorrowTimes = prayerTimesRepository.getPrayerTimesForDate(today.plusDays(1), targetCity, targetCountry).getOrNull()

        // If network queries drop or timeout, fallback gracefully to the
        // user's manual baseline tracking parameters rather than wiping them out with generic system defaults.
        val operationalStart = todayTimes?.maghrib ?: prefs.startTime
        val operationalEnd = tomorrowTimes?.fajr ?: prefs.endTime

        Log.d(TAG, "Tracking window successfully calculated. Windows: $operationalStart -> $operationalEnd")
        return Pair(operationalStart, operationalEnd)
    }

    companion object {
        private const val TAG = "ResolveTrackingWindowUseCase"
    }
}