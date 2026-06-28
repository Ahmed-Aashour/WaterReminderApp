package android.waterreminder.ui.dashboard

import android.waterreminder.data.di.TimeFormat12Hour
import android.waterreminder.data.entity.WaterHistoryEntity
import android.waterreminder.data.repository.WaterRepository
import android.waterreminder.data.store.AppSettingsDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: WaterRepository,
    appSettingsDataStore: AppSettingsDataStore,
    @param:TimeFormat12Hour
    private val timeFormatter: DateTimeFormatter
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<DashboardUiState> = appSettingsDataStore.settingsFlow
        .flatMapLatest { settingsState ->
            val startOfToday = getStartOfToday()
            val targetIntakeGoal = settingsState.dailyGoalMl
            val eligiblePastDaysStart = getPastDaysTimestamp() // Look back 5 weeks for streaks

            // Combine only raw streams needed from the repository
            combine(
                repository.getTodayTotalIntake(startOfToday),
                repository.getTodayHistoryLogs(startOfToday),
                repository.getHistorySince(eligiblePastDaysStart)
            ) { currentIntakeSum, todayLogs, longTermLogs ->

                val now = Calendar.getInstance()
                val todayIndex = now.get(Calendar.DAY_OF_WEEK) - 1 // Sunday = 0

                // 1. Map dynamic database entries into UI layout objects
                val mappedHistory = todayLogs.map { entity ->
                    DrunkCupHistory(
                        id = entity.id,
                        amountMl = entity.amountMl,
                        timeLogged = timeFormatter.format(Instant.ofEpochMilli(entity.timestamp))
                    )
                }

                // 2. Build Sunday-to-Saturday nodes using longTerm logs
                val weeklyNodes = buildWeeklyNodes(longTermLogs, targetIntakeGoal, todayIndex)

                // 3. Calculate streak directly with database metrics
                val computedStreak = calculateStreak(longTermLogs, targetIntakeGoal, currentIntakeSum)

                DashboardUiState.Success(
                    DashboardState(
                        streakSection = StreakSectionState(
                            count = computedStreak,
                            dayIndex = todayIndex,
                            days = weeklyNodes
                        ),
                        historyLogs = mappedHistory,
                        currentIntake = currentIntakeSum, // Calculated by SQLite!
                        targetIntake = targetIntakeGoal
                    )
                )
            }
        }
        .flowOn(Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DashboardUiState.Loading
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

    // Helper properties to keep track of boundaries cleanly on flow emission runs
    private fun getStartOfToday(): Long = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    private fun getPastDaysTimestamp(daysBefore: Int = 35): Long = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_YEAR, -daysBefore)
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    private fun buildWeeklyNodes(
        logs: List<WaterHistoryEntity>,
        target: Int,
        todayIndex: Int
    ): List<StreakDayState> {
        val labels = listOf("S", "M", "Tu", "W", "Th", "F", "S")

        // Group longTerm log buckets ahead of index mapping to speed up time comparisons
        val groupedByDayOfWeek = logs.groupBy { entity ->
            Calendar.getInstance().apply { timeInMillis = entity.timestamp }.get(Calendar.DAY_OF_WEEK) - 1
        }

        return labels.mapIndexed { index, label ->
            when {
                index > todayIndex -> StreakDayState(dayLabel = label, progress = 0.0f)
                else -> {
                    val dayTotal = groupedByDayOfWeek[index]?.sumOf { it.amountMl } ?: 0
                    StreakDayState(
                        dayLabel = label,
                        progress = if (target > 0) (dayTotal.toFloat() / target).coerceAtMost(1.0f) else 0f
                    )
                }
            }
        }
    }

    private fun calculateStreak(logs: List<WaterHistoryEntity>, target: Int, todayTotal: Int): Int {
        var streak = 0
        val checkCalendar = Calendar.getInstance()

        // Index past items for instant mapping
        val dailyTotalsMap = logs.groupBy { entity ->
            val cal = Calendar.getInstance().apply { timeInMillis = entity.timestamp }
            "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.DAY_OF_YEAR)}"
        }.mapValues { entry -> entry.value.sumOf { it.amountMl } }

        // Step backward through timeline consecutively
        while (true) {
            checkCalendar.add(Calendar.DAY_OF_YEAR, -1)
            val key = "${checkCalendar.get(Calendar.YEAR)}-${checkCalendar.get(Calendar.DAY_OF_YEAR)}"
            val dayTotal = dailyTotalsMap[key] ?: 0

            if (dayTotal >= target) {
                streak++
            } else {
                break
            }
        }

        // Apply bonus node value if target goal is completed today
        if (todayTotal >= target) {
            streak++
        }

        return streak
    }
}