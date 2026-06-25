package android.waterreminder.ui.settings

import android.content.Context
import android.waterreminder.data.entity.AppLanguage
import android.waterreminder.data.entity.AppTheme
import android.waterreminder.data.entity.AppUnit
import android.waterreminder.data.store.AppSettingsDataStore
import android.waterreminder.service.WaterNotificationScheduler
import android.waterreminder.service.usecase.ResolveTrackingWindowUseCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appSettingsDataStore: AppSettingsDataStore,
    private val resolveTrackingWindowUseCase: ResolveTrackingWindowUseCase,
    @param:ApplicationContext private val context: Context // To control the scheduler
) : ViewModel() {

    private val _validationErrorChannel = MutableSharedFlow<String>()
    val validationErrorChannel: SharedFlow<String> = _validationErrorChannel.asSharedFlow()
    val predefinedGoalOptions: List<GoalOptionUiModel> = AppSettingsDataStore.PREDEFINED_GOALS_ML.toGoalUiModels(
        AppSettingsDataStore.ML_TO_OZ_FACTOR
    )

    private val notificationScheduler = WaterNotificationScheduler(context)
    private val supportedFrequencies = AppSettingsDataStore.SUPPORTED_FREQUENCIES_MINUTES

    /**
     * Exposes the current read-only snapshot of user settings.
     * Automatically stops active collection when the screen is placed in the background.
     */
    val uiState: StateFlow<SettingsUiState> = appSettingsDataStore.settingsFlow
        .map { prefs ->
            val window = resolveTrackingWindowUseCase.execute(prefs)

            SettingsUiState(
                dailyGoalMl = prefs.dailyGoalMl,
                predefinedGoals = predefinedGoalOptions,
                unit = prefs.unit,
                areNotificationsEnabled = prefs.areNotificationsEnabled,
                frequency = prefs.frequency,
                supportedFrequencies = supportedFrequencies,
                startTime = prefs.startTime,
                endTime = prefs.endTime,
                activeStartTime = window.startTime,
                activeEndTime = window.endTime,
                isFasting = prefs.isFasting,
                theme = prefs.theme,
                language = prefs.language,
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SettingsUiState(
                dailyGoalMl = AppSettingsDataStore.DEFAULT_DAILY_GOAL_ML,
                predefinedGoals = predefinedGoalOptions,
                unit = AppUnit.ML,
                areNotificationsEnabled = AppSettingsDataStore.DEFAULT_ARE_NOTIFICATIONS_ENABLED,
                frequency = AppSettingsDataStore.DEFAULT_FREQUENCY_MINUTES,
                supportedFrequencies = supportedFrequencies,
                startTime = AppSettingsDataStore.DEFAULT_START_TIME,
                endTime = AppSettingsDataStore.DEFAULT_END_TIME,
                activeStartTime = AppSettingsDataStore.DEFAULT_START_TIME,
                activeEndTime = AppSettingsDataStore.DEFAULT_END_TIME,
                isFasting = AppSettingsDataStore.DEFAULT_IS_FASTING,
                theme = AppTheme.SYSTEM,
                language = AppLanguage.ENGLISH,
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

    fun updateUnit(unit: AppUnit) {
        viewModelScope.launch {
            appSettingsDataStore.updateUnit(unit)
        }
    }

    fun updateNotificationToggle(isEnabled: Boolean) {
        viewModelScope.launch {
            appSettingsDataStore.updateNotificationToggle(isEnabled)
            synchronizeScheduler()
        }
    }

    fun updateFrequency(minutes: Int) {
        viewModelScope.launch {
            appSettingsDataStore.updateFrequency(minutes)
            synchronizeScheduler()
        }
    }

    fun updateStartAndEndTimes(startHour: String, endHour: String) {
        viewModelScope.launch {
            appSettingsDataStore.updateStartAndEndTimes(startHour, endHour)
            synchronizeScheduler()
        }
    }

    fun updateFastingState(isFasting: Boolean) {
        viewModelScope.launch {
            appSettingsDataStore.updateFastingState(isFasting)
            synchronizeScheduler()
        }
    }

    fun updateTheme(theme: AppTheme) {
        viewModelScope.launch {
            appSettingsDataStore.updateTheme(theme)
        }
    }

    fun updateLanguage(language: AppLanguage) {
        viewModelScope.launch {
            appSettingsDataStore.updateLanguage(language)
        }
    }

    // --- Helper calculation placeholders ---

    private fun synchronizeScheduler() {
        viewModelScope.launch(Dispatchers.IO) {
            // Thread Safe: Read fresh configuration states directly from data source
            val prefs = appSettingsDataStore.settingsFlow.first()

            if (prefs.areNotificationsEnabled) {
                val window = resolveTrackingWindowUseCase.execute(prefs)

                notificationScheduler.scheduleNextReminder(
                    startTime = window.startTime,
                    endTime = window.endTime,
                    intervalMinutes = prefs.frequency
                )
            } else {
                notificationScheduler.cancelReminders()
            }
        }
    }
}