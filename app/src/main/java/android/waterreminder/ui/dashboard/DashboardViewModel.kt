package android.waterreminder.ui.dashboard

import android.content.Context
import android.waterreminder.R
import android.waterreminder.data.di.TimeFormat12Hour
import android.waterreminder.data.entity.AppUnit
import android.waterreminder.data.entity.WaterHistoryEntity
import android.waterreminder.data.repository.WaterRepository
import android.waterreminder.data.store.AppSettingsDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
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
    private val timeFormatter: DateTimeFormatter,
    @param:ApplicationContext
    private val context: Context
) : ViewModel() {

    private val _validationErrorChannel = Channel<String>(Channel.BUFFERED)
    val validationErrorChannel = _validationErrorChannel.receiveAsFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<DashboardUiState> = appSettingsDataStore.settingsFlow
        .flatMapLatest { settingsState ->
            val startOfToday = getStartOfToday()
            val targetIntakeGoal = settingsState.dailyGoalMl
            val selectedUnit = settingsState.unit
            val eligiblePastDaysStart = getPastDaysTimestamp() // Look back 5 weeks for streaks

            // Combine only raw streams needed from the repository
            combine(
                repository.getTodayTotalIntake(startOfToday),
                repository.getTodayHistoryLogs(startOfToday),
                repository.getHistorySince(eligiblePastDaysStart),
                repository.getCupsCatalog()
            ) { currentIntakeSum, todayLogs, longTermLogs, cupsCatalog ->

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

                val progressFraction = if (targetIntakeGoal > 0) {
                    (currentIntakeSum.toFloat() / targetIntakeGoal.toFloat()).coerceIn(0f, 1f)
                } else {
                    0f
                }

                val progressPercentage = if (targetIntakeGoal > 0) {
                    (currentIntakeSum * 100) / targetIntakeGoal
                } else {
                    0
                }

                val displayState = DisplayIntakeState(
                    currentLabel = selectedUnit.convertFromMl(currentIntakeSum).toString(),
                    targetLabel = selectedUnit.convertFromMl(targetIntakeGoal).toString(),
                    unit = selectedUnit,
                    progressFraction = progressFraction,
                    progressPercentage = progressPercentage
                )

                DashboardUiState.Success(
                    DashboardState(
                        streakSection = StreakSectionState(
                            count = computedStreak,
                            dayIndex = todayIndex,
                            days = weeklyNodes
                        ),
                        historyLogs = mappedHistory,
                        drinkButtons = DrinkButtonsState(
                            unit = selectedUnit,
                            presetAmountsMl = cupsCatalog.map { it.amountMl }
                        ),
                        progressDisplay = displayState
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

    fun addCustomWaterPresetAndLog(
        inputString: String,
        unit: AppUnit,
        onSuccess: () -> Unit
    ) {
        val parsedInt = inputString.trim().toIntOrNull()
        if (parsedInt == null) {
            viewModelScope.launch {
                _validationErrorChannel.send(
                    context.getString(R.string.validation_error_invalid_number)
                )
            }
            return
        }

        viewModelScope.launch {
            // Dynamic custom layout upper bound calculations matching current units
            val minAmount = unit.convertFromMl(AppSettingsDataStore.MIN_CUSTOM_INTAKE_ML)
            val maxAmount = unit.convertFromMl(AppSettingsDataStore.MAX_CUSTOM_INTAKE_ML)

            if (parsedInt !in minAmount..maxAmount) {
                _validationErrorChannel.send(
                    context.getString(
                        R.string.validation_error_out_of_bounds,
                        context.getString(unit.formatRes, minAmount),
                        context.getString(unit.formatRes, maxAmount)
                    )
                )
                return@launch
            }

            // Execution success sequence path
            val amountMl = unit.convertToMl(parsedInt) // Reverse convert back to mL data types for repository storage tracking
            repository.addCupToCatalog(amountMl)
            repository.logWaterConsumption(amountMl)

            onSuccess()
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
