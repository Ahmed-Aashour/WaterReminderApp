package android.waterreminder.data.store

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "water_tracker_prefs")

class WaterDataStore(private val context: Context) {

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
    val settingsFlow: Flow<UserPreferences> = context.dataStore.data
        .map { preferences ->
            UserPreferences(
                dailyGoalMl = preferences[DAILY_GOAL_ML] ?: 2000,
                measurementUnit = preferences[MEASUREMENT_UNIT] ?: "ml",
                isFasting = preferences[IS_FASTING] ?: false,
                notificationInterval = preferences[NOTIFICATION_INTERVAL] ?: 60,
                theme = preferences[THEME] ?: "System",
                language = preferences[LANGUAGE] ?: "English",
                startHour = preferences[REMINDER_START_TIME] ?: "07:00 AM",
                endHour = preferences[REMINDER_END_TIME] ?: "09:00 PM"
            )
        }

    // --- Suspended Preference Write Operations ---

    suspend fun updateDailyGoal(newGoalMl: Int) {
        context.dataStore.edit { prefs -> prefs[DAILY_GOAL_ML] = newGoalMl }
    }

    suspend fun updateMeasurementUnit(unit: String) {
        if (unit in listOf("ml", "oz")) {
            context.dataStore.edit { prefs -> prefs[MEASUREMENT_UNIT] = unit }
        }
    }

    suspend fun updateFastingState(isFasting: Boolean) {
        context.dataStore.edit { prefs -> prefs[IS_FASTING] = isFasting }
    }

    suspend fun updateNotificationInterval(minutes: Int) {
        if (minutes in listOf(30, 60, 90)) {
            context.dataStore.edit { prefs -> prefs[NOTIFICATION_INTERVAL] = minutes }
        }
    }

    suspend fun updateTheme(newTheme: String) {
        if (newTheme in listOf("Light", "Dark", "System")) {
            context.dataStore.edit { prefs -> prefs[THEME] = newTheme }
        }
    }

    suspend fun updateLanguage(newLanguage: String) {
        val validLanguages = listOf("English", "Arabic", "German", "French", "Italian")
        if (newLanguage in validLanguages) {
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

/**
 * Immutable data snapshot representing current preference choices.
 */
data class UserPreferences(
    val dailyGoalMl: Int,
    val measurementUnit: String,
    val isFasting: Boolean,
    val notificationInterval: Int,
    val theme: String,
    val language: String,
    val startHour: String,
    val endHour: String
)