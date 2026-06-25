package android.waterreminder.data.store

import android.content.Context
import android.waterreminder.data.entity.AppLanguage
import android.waterreminder.data.entity.AppTheme
import android.waterreminder.data.entity.AppUnit
import android.waterreminder.data.entity.UserPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "water_tracker_prefs")

class AppSettingsDataStore(private val context: Context) {

    companion object {
        val DAILY_GOAL_ML = intPreferencesKey("daily_goal_ml")
        val UNIT = stringPreferencesKey("unit")
        val ARE_NOTIFICATIONS_ENABLED = booleanPreferencesKey("are_notifications_enabled")
        val FREQUENCY_MINUTES = intPreferencesKey("frequency_minutes")
        val START_TIME = stringPreferencesKey("start_time")
        val END_TIME = stringPreferencesKey("end_time")
        val IS_FASTING = booleanPreferencesKey("is_fasting")
        val CITY = stringPreferencesKey("city")
        val COUNTRY = stringPreferencesKey("country")
        val THEME = stringPreferencesKey("theme")
        val LANGUAGE = stringPreferencesKey("language")


        const val DEFAULT_DAILY_GOAL_ML = 2000
        const val DEFAULT_ARE_NOTIFICATIONS_ENABLED = true
        const val DEFAULT_FREQUENCY_MINUTES = 60
        const val DEFAULT_START_TIME = "07:00 AM"
        const val DEFAULT_END_TIME = "09:00 PM"
        const val DEFAULT_IS_FASTING = false
        const val DEFAULT_FASTING_START_TIME = "06:45 PM"
        const val DEFAULT_FASTING_END_TIME = "04:15 AM"

        const val MIN_DAILY_GOAL_ML = 1000
        const val MAX_DAILY_GOAL_ML = 8000

        const val ML_TO_OZ_FACTOR = 0.0338140227

        val PREDEFINED_GOALS_ML = listOf(2000, 2250, 2500, 2750, 3000)
        val SUPPORTED_FREQUENCIES_MINUTES = listOf(15, 30, 45, 60, 90, 120, 180)
        val SUPPORTED_UNITS = listOf("ml", "fl oz")
    }

    /**
     * Aggregated Settings State Model containing all user configurations.
     */
    val settingsFlow: Flow<UserPreferences> = context.dataStore.data
        .map { preferences ->
            UserPreferences(
                dailyGoalMl = preferences[DAILY_GOAL_ML] ?: DEFAULT_DAILY_GOAL_ML,
                unit = AppUnit.fromKey(preferences[UNIT]),
                areNotificationsEnabled = preferences[ARE_NOTIFICATIONS_ENABLED] ?: DEFAULT_ARE_NOTIFICATIONS_ENABLED,
                frequency = preferences[FREQUENCY_MINUTES] ?: DEFAULT_FREQUENCY_MINUTES,
                startTime = preferences[START_TIME] ?: DEFAULT_START_TIME,
                endTime = preferences[END_TIME] ?: DEFAULT_END_TIME,
                isFasting = preferences[IS_FASTING] ?: DEFAULT_IS_FASTING,
                city = preferences[CITY] ?: "",
                country = preferences[COUNTRY] ?: "",
                theme = AppTheme.fromKey(preferences[THEME]),
                language = AppLanguage.fromIsoCode(preferences[LANGUAGE]),
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

    suspend fun updateUnit(unit: AppUnit) {
        context.dataStore.edit { prefs ->
            prefs[UNIT] = unit.key
        }
    }

    suspend fun updateNotificationToggle(isEnabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[ARE_NOTIFICATIONS_ENABLED] = isEnabled
        }
    }

    suspend fun updateFrequency(minutes: Int) {
        if (minutes in SUPPORTED_FREQUENCIES_MINUTES) {
            context.dataStore.edit { prefs -> prefs[FREQUENCY_MINUTES] = minutes }
        }
    }

    suspend fun updateStartAndEndTimes(startTime: String, endTime: String) {
        context.dataStore.edit { prefs ->
            prefs[START_TIME] = startTime
            prefs[END_TIME] = endTime
        }
    }

    suspend fun updateFastingState(isFasting: Boolean) {
        context.dataStore.edit { prefs -> prefs[IS_FASTING] = isFasting }
    }

    suspend fun updateLocationProfile(city: String, country: String) {
        context.dataStore.edit { prefs ->
            prefs[CITY] = city
            prefs[COUNTRY] = country
        }
    }

    suspend fun updateTheme(newTheme: AppTheme) {
        context.dataStore.edit { prefs ->
            prefs[THEME] = newTheme.key
        }
    }

    suspend fun updateLanguage(newLanguage: AppLanguage) {
        context.dataStore.edit { prefs ->
            prefs[LANGUAGE] = newLanguage.isoCode
        }
    }
}