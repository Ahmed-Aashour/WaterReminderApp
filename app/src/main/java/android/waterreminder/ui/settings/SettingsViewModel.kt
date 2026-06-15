package android.waterreminder.ui.settings

import android.waterreminder.data.store.AppSettingsDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appSettingsDataStore: AppSettingsDataStore
) : ViewModel() {

    private val _validationErrorChannel = MutableSharedFlow<String>()
    val validationErrorChannel: SharedFlow<String> = _validationErrorChannel.asSharedFlow()
    val predefinedGoalOptions: List<GoalOptionUiModel> = AppSettingsDataStore.PREDEFINED_GOALS_ML.toGoalUiModels(
        AppSettingsDataStore.ML_TO_OZ_FACTOR
    )

    val supportedUnits = AppSettingsDataStore.SUPPORTED_UNITS
    private val supportedFrequencies = AppSettingsDataStore.SUPPORTED_FREQUENCIES_MINUTES.toFrequencyUiModels()

    /**
     * Exposes the current read-only snapshot of user settings.
     * Automatically stops active collection when the screen is placed in the background.
     */
    val uiState: StateFlow<SettingsUiState> = appSettingsDataStore.settingsFlow
        .map { prefs ->
            val operationalStart: String
            val operationalEnd: String

            if (prefs.isFasting) {
                operationalStart = fetchTodayMaghribTime()
                operationalEnd = fetchTomorrowFajrTime()
            } else {
                operationalStart = prefs.startTime
                operationalEnd = prefs.endTime
            }

            SettingsUiState(
                dailyGoalMl = prefs.dailyGoalMl,
                unit = prefs.unit,
                isFasting = prefs.isFasting,
                frequency = prefs.frequency,
                theme = prefs.theme,
                language = prefs.language,
                startTime = prefs.startTime,
                endTime = prefs.endTime,
                activeStartTime = operationalStart,
                activeEndTime = operationalEnd,
                predefinedGoals = predefinedGoalOptions,
                supportedUnits = supportedUnits,
                supportedFrequencies = supportedFrequencies
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SettingsUiState(
                dailyGoalMl = AppSettingsDataStore.DEFAULT_DAILY_GOAL_ML,
                unit = AppSettingsDataStore.DEFAULT_UNIT,
                isFasting = AppSettingsDataStore.DEFAULT_IS_FASTING,
                frequency = AppSettingsDataStore.DEFAULT_FREQUENCY_MINUTES,
                theme = AppSettingsDataStore.DEFAULT_THEME,
                language = AppSettingsDataStore.DEFAULT_LANGUAGE,
                startTime = AppSettingsDataStore.DEFAULT_START_TIME,
                endTime = AppSettingsDataStore.DEFAULT_END_TIME,
                activeStartTime = AppSettingsDataStore.DEFAULT_START_TIME,
                activeEndTime = AppSettingsDataStore.DEFAULT_END_TIME,
                predefinedGoals = predefinedGoalOptions,
                supportedUnits = supportedUnits,
                supportedFrequencies = supportedFrequencies
            )
        )

    // --- Dynamic User Settings Action Setters ---

    fun updateCustomDailyGoalString(inputString: String) {
        val parsedInt = inputString.trim().toIntOrNull()
        if (parsedInt == null) {
            viewModelScope.launch {
                _validationErrorChannel.emit("Please enter a valid numeric value.")
            }
            return
        }
        updateDailyGoal(parsedInt)
    }

    fun updateDailyGoal(goalMl: Int) {
        viewModelScope.launch {
            val wasSaved = appSettingsDataStore.updateDailyGoal(goalMl)
            if (!wasSaved) {
                _validationErrorChannel.emit(
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

    fun updateFrequency(minutes: Int) {
        viewModelScope.launch {
            appSettingsDataStore.updateFrequency(minutes)
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

    fun updateStartAndEndTimes(startHour: String, endHour: String) {
        viewModelScope.launch {
            appSettingsDataStore.updateStartAndEndTimes(startHour, endHour)
        }
    }

    // --- Helper calculation placeholders ---
    // TODO: Embed PrayerTimesService Implementation
    private fun fetchTodayMaghribTime(): String = "06:45 PM"
    private fun fetchTomorrowFajrTime(): String = "04:15 AM"
}