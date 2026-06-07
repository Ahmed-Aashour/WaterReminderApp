package android.waterreminder.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.waterreminder.data.entity.WaterHistoryEntity
import android.waterreminder.data.repository.WaterRepository
import android.waterreminder.data.store.AppSettingsDataStore
import android.waterreminder.data.store.SettingsConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: WaterRepository,
    private val appSettingsDataStore: AppSettingsDataStore
) : ViewModel() {

    // Define a hardcoded target intake goal for now (2000ml)
    private val targetIntakeGoal = 2000

    // Time-formatter for converting raw timestamps into clean UI strings
    private val timeFormatter = SimpleDateFormat("hh:mm A", Locale.getDefault())

    val uiState: StateFlow<DashboardState> = combine(
        repository.getCupsCatalog(),
        repository.getHistoryForPastDays(daysBefore = 7),  // For the weekly nodes UI
        repository.getHistoryForPastDays(daysBefore = 35), // Efficient 5-week look-back window for streak calculation
        appSettingsDataStore.settingsFlow
    ) { catalog, currentWeekLogs, longTermLogs, settingsState ->

        val targetIntakeGoal = settingsState.dailyGoalMl

        val now = Calendar.getInstance()
        val todayIndex = now.get(Calendar.DAY_OF_WEEK) - 1 // Sunday = 0, Monday = 1, etc.

        // 1. Calculate today's real-time intake sum
        val startOfToday = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val currentIntakeSum = currentWeekLogs
            .filter { it.timestamp >= startOfToday }
            .sumOf { it.amountMl }

        // 2. Format database entities into UI history rows
        val mappedHistory = currentWeekLogs
            .filter { it.timestamp >= startOfToday }
            .map { entity ->
                DrunkCupHistory(
                    id = entity.id,
                    amountMl = entity.amountMl,
                    timeLogged = timeFormatter.format(entity.timestamp)
                )
            }

        // 3. Build Sunday-to-Saturday Week Nodes
        val weeklyNodes = buildWeeklyNodes(currentWeekLogs, targetIntakeGoal, todayIndex)

        // 4. Compute Current Consecutive Streak Counter
        val computedStreak = calculateStreak(longTermLogs, targetIntakeGoal)

        // Return the clean, fully calculated UI State
        DashboardState(
            streakSection = StreakSectionState(
                count = computedStreak,
                dayIndex = todayIndex,
                days = weeklyNodes
            ),
            historyLogs = mappedHistory,
            currentIntake = currentIntakeSum,
            targetIntake = targetIntakeGoal
        )
    }
        .flowOn(Dispatchers.Default) // Ensures math calculations never run on the UI thread!
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DashboardState(
                streakSection = StreakSectionState(
                    count = 0,
                    dayIndex = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1,
                    days = emptyList()
                ),
                historyLogs = emptyList(),
                currentIntake = 0,
                targetIntake = SettingsConfig.DEFAULT_DAILY_GOAL_ML
            )
        )

    // --- Interactive User UI Actions ---

    fun logWater(amountMl: Int) {
        viewModelScope.launch {
            repository.logWaterConsumption(amountMl)
        }
    }

    fun deleteWaterLog(log: DrunkCupHistory) {
        viewModelScope.launch {
            repository.deleteWaterLog(log.id)
        }
    }

    // --- Core Calculation Helper Functions ---

    private fun buildWeeklyNodes(
        logs: List<WaterHistoryEntity>,
        target: Int,
        todayIndex: Int
    ): List<StreakDayState> {
        val labels = listOf("S", "M", "Tu", "W", "Th", "F", "S")

        return labels.mapIndexed { index, label ->
            if (index > todayIndex) {
                // Future day: instantly progress 0.0f without searching the database
                StreakDayState(dayLabel = label, progress = 0.0f)
            } else {
                // Find target bounds for this specific weekday row
                val dayCalendar = Calendar.getInstance().apply {
                    set(Calendar.DAY_OF_WEEK, index + 1)
                    set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                }
                val startOfDay = dayCalendar.timeInMillis
                val endOfDay = startOfDay + 86400000L // Add 24 hours in milliseconds

                val dayTotal = logs
                    .filter { it.timestamp in startOfDay until endOfDay }
                    .sumOf { it.amountMl }

                StreakDayState(
                    dayLabel = label,
                    progress = (dayTotal.toFloat() / target).coerceAtMost(1.0f)
                )
            }
        }
    }

    private fun calculateStreak(logs: List<WaterHistoryEntity>, target: Int): Int {
        var streak = 0
        val checkCalendar = Calendar.getInstance()

        // Group all logs within our look-back window into a quick-access map grouped by local day representation
        val dailyTotalsMap = logs.groupBy { entity ->
            val cal = Calendar.getInstance().apply { timeInMillis = entity.timestamp }
            "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.DAY_OF_YEAR)}"
        }.mapValues { entry -> entry.value.sumOf { it.amountMl } }

        // Start checking backwards from yesterday
        while (true) {
            checkCalendar.add(Calendar.DAY_OF_YEAR, -1)
            val key = "${checkCalendar.get(Calendar.YEAR)}-${checkCalendar.get(Calendar.DAY_OF_YEAR)}"
            val dayTotal = dailyTotalsMap[key] ?: 0

            if (dayTotal >= target) {
                streak++
            } else {
                break // The chain has broken, stop searching immediately!
            }
        }

        // Add a bonus point to the count if the user has already completed today's target!
        val todayKey = "${Calendar.getInstance().get(Calendar.YEAR)}-${Calendar.getInstance().get(Calendar.DAY_OF_YEAR)}"
        val todayTotal = dailyTotalsMap[todayKey] ?: 0
        if (todayTotal >= target) {
            streak++
        }

        return streak
    }
}