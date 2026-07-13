package android.waterreminder.ui.settings

import android.content.Context
import android.util.Log
import android.waterreminder.R
import android.waterreminder.data.entity.AppLanguage
import android.waterreminder.data.entity.AppTheme
import android.waterreminder.data.entity.AppUnit
import android.waterreminder.data.store.AppSettingsDataStore
import android.waterreminder.service.HydrationReminderScheduler
import android.waterreminder.service.usecase.ResolveTrackingWindowUseCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appSettingsDataStore: AppSettingsDataStore,
    private val resolveTrackingWindowUseCase: ResolveTrackingWindowUseCase,
    @param:ApplicationContext private val context: Context // To control the scheduler
) : ViewModel() {

    private val _validationErrorChannel = Channel<String>(Channel.BUFFERED)
    val validationErrorChannel = _validationErrorChannel.receiveAsFlow()
    val predefinedGoalOptions: List<Int> = AppSettingsDataStore.PREDEFINED_GOALS_ML

    private val notificationScheduler = HydrationReminderScheduler(context)
    private val supportedFrequencies = AppSettingsDataStore.SUPPORTED_FREQUENCIES_MINUTES

    /**
     * Exposes the current read-only snapshot of user settings.
     * Automatically stops active collection when the screen is placed in the background.
     */
    val uiState: StateFlow<SettingsUiState> = appSettingsDataStore.settingsFlow
        .map { prefs ->
            val window = resolveTrackingWindowUseCase.execute(prefs)

            Log.d(
                TAG,
                "uiState updating: goal=${prefs.dailyGoalMl}ml, unit=${prefs.unit}, " +
                        "notifications=${prefs.areNotificationsEnabled}, freq=${prefs.frequency}m, " +
                        "time=[${prefs.startTime}-${prefs.endTime}], activeWindow=[${window.first}-${window.second}], " +
                        "fasting=${prefs.isFasting}, theme=${prefs.theme}, lang=${prefs.language}"
            )

            SettingsUiState(
                dailyGoalMl = prefs.dailyGoalMl,
                predefinedGoals = predefinedGoalOptions,
                unit = prefs.unit,
                areNotificationsEnabled = prefs.areNotificationsEnabled,
                frequency = prefs.frequency,
                supportedFrequencies = supportedFrequencies,
                startTime = prefs.startTime,
                endTime = prefs.endTime,
                activeStartTime = window.first,
                activeEndTime = window.second,
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
        Log.d(TAG, "updateCustomDailyGoalString: input=\"$inputString\"")
        val parsedInt = inputString.trim().toIntOrNull()
        if (parsedInt == null) {
            Log.d(TAG, "updateCustomDailyGoalString: parsing failed, sending validation error")
            viewModelScope.launch {
                _validationErrorChannel.send(
                    context.getString(R.string.validation_error_invalid_number)
                )
            }
            return
        }
        updateDailyGoal(parsedInt)
    }

    fun updateDailyGoal(goalMl: Int) {
        Log.d(TAG, "updateDailyGoal: request value=${goalMl}ml")
        viewModelScope.launch {
            val wasSaved = appSettingsDataStore.updateDailyGoal(goalMl)
            if (!wasSaved) {
                Log.d(TAG, "updateDailyGoal: rejected by datastore (out of bounds)")
                // Fetch the user's selected unit to display localized values, or fall back to plain text strings if context demands
                val currentUnit = uiState.value.unit
                val minDisplay = context.getString(
                    currentUnit.formatRes,
                    currentUnit.convertFromMl(AppSettingsDataStore.MIN_DAILY_GOAL_ML)
                )
                val maxDisplay = context.getString(
                    currentUnit.formatRes,
                    currentUnit.convertFromMl(AppSettingsDataStore.MAX_DAILY_GOAL_ML)
                )

                _validationErrorChannel.send(
                    context.getString(
                        R.string.validation_error_out_of_bounds,
                        minDisplay,
                        maxDisplay
                    )
                )
            }
        }
    }

    fun updateUnit(unit: AppUnit) {
        Log.d(TAG, "updateUnit: target unit=$unit")
        viewModelScope.launch {
            appSettingsDataStore.updateUnit(unit)
        }
    }

    fun updateNotificationToggle(isEnabled: Boolean) {
        Log.d(TAG, "updateNotificationToggle: isEnabled=$isEnabled")
        viewModelScope.launch {
            appSettingsDataStore.updateNotificationToggle(isEnabled)
            synchronizeScheduler()
        }
    }

    fun updateFrequency(minutes: Int) {
        Log.d(TAG, "updateFrequency: target minutes=$minutes")
        viewModelScope.launch {
            appSettingsDataStore.updateFrequency(minutes)
            synchronizeScheduler()
        }
    }

    fun updateStartAndEndTimes(startHour: LocalTime, endHour: LocalTime) {
        Log.d(TAG, "updateStartAndEndTimes: start=$startHour, end=$endHour")
        viewModelScope.launch {
            appSettingsDataStore.updateStartAndEndTimes(startHour, endHour)
            synchronizeScheduler()
        }
    }

    fun updateFastingState(isFasting: Boolean) {
        Log.d(TAG, "updateFastingState: isFasting=$isFasting")
        viewModelScope.launch {
            appSettingsDataStore.updateFastingState(isFasting)
            synchronizeScheduler()
        }
    }

    fun updateTheme(theme: AppTheme) {
        Log.d(TAG, "updateTheme: target theme=$theme")
        viewModelScope.launch {
            appSettingsDataStore.updateTheme(theme)
        }
    }

    fun updateLanguage(language: AppLanguage) {
        Log.d(TAG, "updateLanguage: target language=$language")
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
                Log.d(
                    TAG,
                    "synchronizeScheduler: Scheduling tracking window [${window.first} - ${window.second}] every ${prefs.frequency}m"
                )

                notificationScheduler.scheduleNextReminder(
                    startTime = window.first,
                    endTime = window.second,
                    intervalMinutes = prefs.frequency
                )
            } else {
                Log.d(TAG, "synchronizeScheduler: Notifications disabled, cancelling active reminders")
                notificationScheduler.cancelReminders()
            }
        }
    }

    companion object {
        private const val TAG = "SettingsViewModel"
    }
}