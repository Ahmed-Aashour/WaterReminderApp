package android.waterreminder.data.store

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "water_tracker_prefs")

class AppSettingsDataStore(private val context: Context) {

    companion object {
        // Core Target and Metrics Keys
        val DAILY_GOAL_ML = intPreferencesKey("daily_goal_ml")
        val MEASUREMENT_UNIT = stringPreferencesKey("measurement_unit")
        val IS_FASTING = booleanPreferencesKey("is_fasting")

        // Polling Window & Engine Keys
        val NOTIFICATION_INTERVAL = intPreferencesKey("notification_interval")
        val THEME = stringPreferencesKey("theme")
        val LANGUAGE = stringPreferencesKey("language")

        // Reminder Frame Boundary Constraints (e.g. "07:00 AM")
        val REMINDER_START_TIME = stringPreferencesKey("reminder_start_time")
        val REMINDER_END_TIME = stringPreferencesKey("reminder_end_time")
    }

    /**
     * Aggregated Settings State Model containing all user configurations.
     */
    val settingsFlow: Flow<SettingsState> = context.dataStore.data
        .map { preferences ->
            val isFastingActive = preferences[IS_FASTING] ?: SettingsConfig.DEFAULT_IS_FASTING

            // Fetch baseline disk states safely
            val originalStart = preferences[REMINDER_START_TIME] ?: SettingsConfig.DEFAULT_START_TIME
            val originalEnd = preferences[REMINDER_END_TIME] ?: SettingsConfig.DEFAULT_END_TIME

            // 🌟 Compute operational bounds dynamically
            val operationalStart: String
            val operationalEnd: String

            if (isFastingActive) {
                // TODO: Fetch these dynamically from a PrayerTimes calculation library based on device GPS location
                operationalStart = fetchTodayMaghribTime() // e.g., "06:45 PM"
                operationalEnd = fetchTomorrowFajrTime()    // e.g., "04:15 AM"
            } else {
                operationalStart = originalStart
                operationalEnd = originalEnd
            }

            SettingsState(
                dailyGoalMl = preferences[DAILY_GOAL_ML] ?: SettingsConfig.DEFAULT_DAILY_GOAL_ML,
                measurementUnit = preferences[MEASUREMENT_UNIT] ?: SettingsConfig.DEFAULT_MEASUREMENT_UNIT,
                isFasting = isFastingActive,
                notificationInterval = preferences[NOTIFICATION_INTERVAL] ?: SettingsConfig.DEFAULT_NOTIFICATION_INTERVAL_MIN,
                theme = preferences[THEME] ?: SettingsConfig.DEFAULT_THEME,
                language = preferences[LANGUAGE] ?: SettingsConfig.DEFAULT_LANGUAGE,
                savedStartHour = originalStart, // Kept safe & unchanged
                savedEndHour = originalEnd,     // Kept safe & unchanged
                activeStartHour = operationalStart, // Used by notification workers
                activeEndHour = operationalEnd      // Used by notification workers
            )
        }

    // --- Suspended Preference Write Operations ---

    suspend fun updateDailyGoal(newGoalMl: Int) {
        context.dataStore.edit { prefs -> prefs[DAILY_GOAL_ML] = newGoalMl }
    }

    suspend fun updateMeasurementUnit(unit: String) {
        if (unit in SettingsConfig.SUPPORTED_UNITS) {
            context.dataStore.edit { prefs -> prefs[MEASUREMENT_UNIT] = unit }
        }
    }

    suspend fun updateFastingState(isFasting: Boolean) {
        context.dataStore.edit { prefs -> prefs[IS_FASTING] = isFasting }
    }

    suspend fun updateNotificationInterval(minutes: Int) {
        val sanitizedMinutes = minutes.coerceIn(
            SettingsConfig.MIN_NOTIFICATION_INTERVAL_MIN,
            SettingsConfig.MAX_NOTIFICATION_INTERVAL_MIN
        )
        context.dataStore.edit { prefs ->
            prefs[NOTIFICATION_INTERVAL] = sanitizedMinutes
        }
    }

    suspend fun updateTheme(newTheme: String) {
        if (newTheme in SettingsConfig.SUPPORTED_THEMES) {
            context.dataStore.edit { prefs -> prefs[THEME] = newTheme }
        }
    }

    suspend fun updateLanguage(newLanguage: String) {
        if (newLanguage in SettingsConfig.SUPPORTED_LANGUAGES) {
            context.dataStore.edit { prefs -> prefs[LANGUAGE] = newLanguage }
        }
    }

    suspend fun updateReminderWindow(startHour: String, endHour: String) {
        context.dataStore.edit { prefs ->
            prefs[REMINDER_START_TIME] = startHour
            prefs[REMINDER_END_TIME] = endHour
        }
    }

    // --- Helper calculation placeholders ---
    private fun fetchTodayMaghribTime(): String = "06:45 PM"
    private fun fetchTomorrowFajrTime(): String = "04:15 AM"
}