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
        val DAILY_GOAL_ML = intPreferencesKey("daily_goal_ml")
        val MEASUREMENT_UNIT = stringPreferencesKey("measurement_unit")
        val IS_FASTING = booleanPreferencesKey("is_fasting")
        val NOTIFICATION_INTERVAL = intPreferencesKey("notification_interval")
        val THEME = stringPreferencesKey("theme")
        val LANGUAGE = stringPreferencesKey("language")
        val REMINDER_START_TIME = stringPreferencesKey("reminder_start_time")
        val REMINDER_END_TIME = stringPreferencesKey("reminder_end_time")


        const val DEFAULT_DAILY_GOAL_ML = 2000
        const val DEFAULT_MEASUREMENT_UNIT = "ml"
        const val DEFAULT_IS_FASTING = false
        const val DEFAULT_NOTIFICATION_INTERVAL_MIN = 60
        const val DEFAULT_THEME = "System"
        const val DEFAULT_LANGUAGE = "English"
        const val DEFAULT_START_TIME = "07:00 AM"
        const val DEFAULT_END_TIME = "09:00 PM"

        const val MIN_NOTIFICATION_INTERVAL_MIN = 15
        const val MAX_NOTIFICATION_INTERVAL_MIN = 180
        const val MIN_DAILY_GOAL_ML = 1000
        const val MAX_DAILY_GOAL_ML = 8000

        const val ML_TO_OZ_FACTOR = 0.0338140227

        val PREDEFINED_GOALS_ML = listOf(2000, 2250, 2500, 2750, 3000)
        val SUPPORTED_THEMES = listOf("Light", "Dark", "System")
        val SUPPORTED_LANGUAGES = listOf("English", "Arabic", "German", "French", "Italian")
        val SUPPORTED_UNITS = listOf("ml", "oz")
    }

    /**
     * Aggregated Settings State Model containing all user configurations.
     */
    val settingsFlow: Flow<UserPreferences> = context.dataStore.data
        .map { preferences ->
            UserPreferences(
                dailyGoalMl = preferences[DAILY_GOAL_ML] ?: DEFAULT_DAILY_GOAL_ML,
                measurementUnit = preferences[MEASUREMENT_UNIT] ?: DEFAULT_MEASUREMENT_UNIT,
                isFasting = preferences[IS_FASTING] ?: DEFAULT_IS_FASTING,
                notificationInterval = preferences[NOTIFICATION_INTERVAL] ?: DEFAULT_NOTIFICATION_INTERVAL_MIN,
                theme = preferences[THEME] ?: DEFAULT_THEME,
                language = preferences[LANGUAGE] ?: DEFAULT_LANGUAGE,
                savedStartHour = preferences[REMINDER_START_TIME] ?: DEFAULT_START_TIME,
                savedEndHour = preferences[REMINDER_END_TIME] ?: DEFAULT_END_TIME
            )
        }

    // --- Suspended Preference Write Operations ---

    suspend fun updateDailyGoal(newGoalMl: Int): Boolean {
        if (newGoalMl !in MIN_DAILY_GOAL_ML..MAX_DAILY_GOAL_ML) {
            return false
        }
        context.dataStore.edit { prefs -> prefs[DAILY_GOAL_ML] = newGoalMl }
        return true
    }

    suspend fun updateMeasurementUnit(unit: String) {
        if (unit in SUPPORTED_UNITS) {
            context.dataStore.edit { prefs -> prefs[MEASUREMENT_UNIT] = unit }
        }
    }

    suspend fun updateFastingState(isFasting: Boolean) {
        context.dataStore.edit { prefs -> prefs[IS_FASTING] = isFasting }
    }

    suspend fun updateNotificationInterval(minutes: Int) {
        // Safe internal sanitization using co-located boundary constraints
        val sanitizedMinutes = minutes.coerceIn(
            MIN_NOTIFICATION_INTERVAL_MIN,
            MAX_NOTIFICATION_INTERVAL_MIN
        )
        context.dataStore.edit { prefs ->
            prefs[NOTIFICATION_INTERVAL] = sanitizedMinutes
        }
    }

    suspend fun updateTheme(newTheme: String) {
        if (newTheme in SUPPORTED_THEMES) {
            context.dataStore.edit { prefs -> prefs[THEME] = newTheme }
        }
    }

    suspend fun updateLanguage(newLanguage: String) {
        if (newLanguage in SUPPORTED_LANGUAGES) {
            context.dataStore.edit { prefs -> prefs[LANGUAGE] = newLanguage }
        }
    }

    suspend fun updateReminderWindow(startHour: String, endHour: String) {
        context.dataStore.edit { prefs ->
            prefs[REMINDER_START_TIME] = startHour
            prefs[REMINDER_END_TIME] = endHour
        }
    }
}