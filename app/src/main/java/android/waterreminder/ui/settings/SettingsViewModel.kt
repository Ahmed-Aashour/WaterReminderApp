package android.waterreminder.ui.settings

import android.content.Context
import android.waterreminder.data.entity.DayPrayerTimes
import android.waterreminder.data.repository.PrayerTimesRepository
import android.waterreminder.data.store.AppSettingsDataStore
import android.waterreminder.service.WaterNotificationScheduler
import android.waterreminder.service.utils.TimeUtils
import android.waterreminder.service.utils.TimeUtils.timeFormatter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appSettingsDataStore: AppSettingsDataStore,
    private val prayerTimesRepository: PrayerTimesRepository,
    @param:ApplicationContext private val context: Context // To control the scheduler
) : ViewModel() {

    private val _validationErrorChannel = MutableSharedFlow<String>()
    val validationErrorChannel: SharedFlow<String> = _validationErrorChannel.asSharedFlow()
    val predefinedGoalOptions: List<GoalOptionUiModel> = AppSettingsDataStore.PREDEFINED_GOALS_ML.toGoalUiModels(
        AppSettingsDataStore.ML_TO_OZ_FACTOR
    )

    val supportedUnits = AppSettingsDataStore.SUPPORTED_UNITS
    private val notificationScheduler = WaterNotificationScheduler(context)
    private val supportedFrequencies = AppSettingsDataStore.SUPPORTED_FREQUENCIES_MINUTES.toFrequencyUiModels()

    // Reactive holder for cached dynamic prayer data fetches
    private val _todayPrayerTimes = MutableStateFlow<DayPrayerTimes?>(null)
    private val _tomorrowPrayerTimes = MutableStateFlow<DayPrayerTimes?>(null)

    init {
        fetchDynamicPrayerTimesPipeline()
    }

    /**
     * Exposes the current read-only snapshot of user settings.
     * Automatically stops active collection when the screen is placed in the background.
     */
    val uiState: StateFlow<SettingsUiState> = combine(
        appSettingsDataStore.settingsFlow,
        _todayPrayerTimes,
        _tomorrowPrayerTimes
    ) { prefs, todayTimes, tomorrowTimes ->
        val operationalStart = if (prefs.isFasting) {
            todayTimes?.maghrib?.format(timeFormatter) ?: AppSettingsDataStore.DEFAULT_FASTING_START_TIME
        } else {
            prefs.startTime
        }

        val operationalEnd = if (prefs.isFasting) {
            tomorrowTimes?.fajr?.format(timeFormatter) ?: AppSettingsDataStore.DEFAULT_FASTING_END_TIME
        } else {
            prefs.endTime
        }

        SettingsUiState(
            dailyGoalMl = prefs.dailyGoalMl,
            predefinedGoals = predefinedGoalOptions,
            unit = prefs.unit,
            supportedUnits = supportedUnits,
            areNotificationsEnabled = prefs.areNotificationsEnabled,
            frequency = prefs.frequency,
            supportedFrequencies = supportedFrequencies,
            startTime = prefs.startTime,
            endTime = prefs.endTime,
            activeStartTime = operationalStart,
            activeEndTime = operationalEnd,
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
            unit = AppSettingsDataStore.DEFAULT_UNIT,
            supportedUnits = supportedUnits,
            areNotificationsEnabled = AppSettingsDataStore.DEFAULT_ARE_NOTIFICATIONS_ENABLED,
            frequency = AppSettingsDataStore.DEFAULT_FREQUENCY_MINUTES,
            supportedFrequencies = supportedFrequencies,
            startTime = AppSettingsDataStore.DEFAULT_START_TIME,
            endTime = AppSettingsDataStore.DEFAULT_END_TIME,
            activeStartTime = AppSettingsDataStore.DEFAULT_START_TIME,
            activeEndTime = AppSettingsDataStore.DEFAULT_END_TIME,
            isFasting = AppSettingsDataStore.DEFAULT_IS_FASTING,
            theme = AppSettingsDataStore.DEFAULT_THEME,
            language = AppSettingsDataStore.DEFAULT_LANGUAGE,
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

    fun updateUnit(unit: String) {
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

    // --- Helper calculation placeholders ---

    private fun synchronizeScheduler() {
        viewModelScope.launch {
            val currentState = uiState.value
            if (currentState.areNotificationsEnabled) {
                notificationScheduler.scheduleNextReminder(
                    startTime = currentState.activeStartTime,
                    endTime = currentState.activeEndTime,
                    intervalMinutes = currentState.frequency
                )
            } else {
                notificationScheduler.cancelReminders()
            }
        }
    }

    private fun fetchDynamicPrayerTimesPipeline() {
        viewModelScope.launch(Dispatchers.IO) {
            // TODO: Extract actual strings dynamically from Location Services or user profile preferences
            val targetCity = "Alexandria"
            val targetCountry = "Egypt"

            val todayResult = prayerTimesRepository.getPrayerTimesForDate(LocalDate.now(), targetCity, targetCountry)
            todayResult.onSuccess { _todayPrayerTimes.value = it }

            val tomorrowResult = prayerTimesRepository.getPrayerTimesForDate(LocalDate.now().plusDays(1), targetCity, targetCountry)
            tomorrowResult.onSuccess { _tomorrowPrayerTimes.value = it }
        }
    }
}