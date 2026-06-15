package android.waterreminder.ui.dashboard

sealed interface DashboardUiState {
    object Loading : DashboardUiState
    data class Success(val data: DashboardState) : DashboardUiState
}

// A clean wrapper mapping all varying data combinations together
data class DashboardState(
    val streakSection: StreakSectionState,
    val historyLogs: List<DrunkCupHistory>,
    val currentIntake: Int,
    val targetIntake: Int
)

data class StreakSectionState(
    val count: Int,
    val dayIndex: Int,
    val days: List<StreakDayState>
)

data class StreakDayState(
    val dayLabel: String,     // "S", "M", "Tu", "W", "Th", "F", "S"
    val progress: Float,      // 0.0f to 1.0f+
)

/**
 * Data representation matching the grouped preset structure identified in the Figma spec.
 */
data class DrunkCupHistory(
    val id: Long,
    val amountMl: Int,
    val timeLogged: String,
)