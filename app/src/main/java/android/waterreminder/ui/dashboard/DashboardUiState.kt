package android.waterreminder.ui.dashboard

// A clean wrapper mapping all varying data combinations together
data class DashboardState(
    val streakDays: List<StreakDayState>,
    val historyLogs: List<DrunkCupHistory>,
    val currentIntake: Int,
    val targetIntake: Int,
    val streakCount: Int,
    val currentDayIndex: Int,
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