package android.waterreminder.ui.dashboard

import android.waterreminder.ui.model.DrunkCupHistory

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