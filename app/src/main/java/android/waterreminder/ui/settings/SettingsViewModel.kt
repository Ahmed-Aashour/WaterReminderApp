package android.waterreminder.ui.settings

import android.waterreminder.data.store.AppSettingsDataStore
import android.waterreminder.data.store.SettingsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appSettingsDataStore: AppSettingsDataStore
) : ViewModel() {

    private val _goalValidationErrorChannel = MutableSharedFlow<String>()
    val goalValidationErrorChannel: SharedFlow<String> = _goalValidationErrorChannel.asSharedFlow()

    val predefinedGoalOptions: List<GoalOptionUiModel> = AppSettingsDataStore.PREDEFINED_GOALS_ML.map { ml ->
        val ozCalculated = (ml * AppSettingsDataStore.ML_TO_OZ_FACTOR).roundToInt()
        GoalOptionUiModel(
            amountMl = ml,
            displayLabelMl = "$ml ml",
            displayLabelOz = "$ozCalculated fl oz"
        )
    }

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

    fun updateCustomDailyGoalString(inputString: String) {
        val parsedInt = inputString.trim().toIntOrNull()
        if (parsedInt == null) {
            viewModelScope.launch {
                _goalValidationErrorChannel.emit("Please enter a valid numeric value.")
            }
            return
        }
        updateDailyGoal(parsedInt)
    }

    fun updateDailyGoal(goalMl: Int) {
        viewModelScope.launch {
            val wasSaved = appSettingsDataStore.updateDailyGoal(goalMl)
            if (!wasSaved) {
                _goalValidationErrorChannel.emit(
                    "Goal must be between ${AppSettingsDataStore.MIN_DAILY_GOAL_ML}ml and ${AppSettingsDataStore.MAX_DAILY_GOAL_ML}ml."
                )
            }
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