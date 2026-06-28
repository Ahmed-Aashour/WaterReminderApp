package android.waterreminder.ui.dashboard

import android.waterreminder.data.entity.AppUnit

sealed interface DashboardUiState {
    object Loading : DashboardUiState
    data class Success(val data: DashboardState) : DashboardUiState
}

// A clean wrapper mapping all varying data combinations together
data class DashboardState(
    val streakSection: StreakSectionState,
    val historyLogs: List<DrunkCupHistory>,
    val progressDisplay: DisplayIntakeState
)

data class DisplayIntakeState(
    val currentLabel: String,      // e.g., "1500" or "50"
    val targetLabel: String,       // e.g., "2000" or "68"
    val unit: AppUnit,             // Target unit configuration
    val progressFraction: Float,   // e.g., 0.75f for layout bounds filling
    val progressPercentage: Int    // e.g., 75 for text display tracking
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