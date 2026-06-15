package android.waterreminder.ui.settings

import android.waterreminder.data.store.AppSettingsDataStore
import android.waterreminder.data.store.SettingsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appSettingsDataStore: AppSettingsDataStore
) : ViewModel() {

    /**
     * Exposes the current read-only snapshot of user settings.
     * Automatically stops active collection when the screen is placed in the background.
     */
    val uiState: StateFlow<SettingsState> = appSettingsDataStore.settingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SettingsState(
                dailyGoalMl = AppSettingsDataStore.DEFAULT_DAILY_GOAL_ML,
                measurementUnit = AppSettingsDataStore.DEFAULT_MEASUREMENT_UNIT,
                isFasting = AppSettingsDataStore.DEFAULT_IS_FASTING,
                notificationInterval = AppSettingsDataStore.DEFAULT_NOTIFICATION_INTERVAL_MIN,
                theme = AppSettingsDataStore.DEFAULT_THEME,
                language = AppSettingsDataStore.DEFAULT_LANGUAGE,
                savedStartHour = AppSettingsDataStore.DEFAULT_START_TIME,
                savedEndHour = AppSettingsDataStore.DEFAULT_END_TIME,
                activeStartHour = AppSettingsDataStore.DEFAULT_START_TIME,
                activeEndHour = AppSettingsDataStore.DEFAULT_END_TIME
            )
        )

    // --- Dynamic User Settings Action Setters ---

    fun updateDailyGoal(goalMl: Int) {
        viewModelScope.launch {
            appSettingsDataStore.updateDailyGoal(goalMl)
        }
    }

    fun updateMeasurementUnit(unit: String) {
        viewModelScope.launch {
            appSettingsDataStore.updateMeasurementUnit(unit)
        }
    }

    fun updateFastingState(isFasting: Boolean) {
        viewModelScope.launch {
            appSettingsDataStore.updateFastingState(isFasting)
        }
    }

    fun updateNotificationInterval(minutes: Int) {
        viewModelScope.launch {
            appSettingsDataStore.updateNotificationInterval(minutes)
        }
    }

    fun updateTheme(theme: String) {
        viewModelScope.launch {
            appSettingsDataStore.updateTheme(theme)
        }
    }

    fun updateLanguage(language: String) {
        viewModelScope.launch {
            appSettingsDataStore.updateLanguage(language)
        }
    }

    fun updateReminderWindow(startHour: String, endHour: String) {
        viewModelScope.launch {
            appSettingsDataStore.updateReminderWindow(startHour, endHour)
        }
    }
}