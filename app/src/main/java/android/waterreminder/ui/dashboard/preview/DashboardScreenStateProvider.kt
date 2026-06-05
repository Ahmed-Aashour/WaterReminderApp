package android.waterreminder.ui.dashboard.preview

import android.waterreminder.ui.core.components.StreakDayState
import android.waterreminder.ui.dashboard.components.DrunkCupHistory
import androidx.compose.ui.tooling.preview.PreviewParameterProvider

// A clean wrapper mapping all varying data combinations together
data class DashboardPreviewState(
    val streakDays: List<StreakDayState>,
    val historyLogs: List<DrunkCupHistory>,
    val currentIntake: Int,
    val targetIntake: Int,
    val streakCount: Int
)

class DashboardScreenStateProvider : PreviewParameterProvider<DashboardPreviewState> {
    override val values: Sequence<DashboardPreviewState> = sequenceOf(
        // State 1: Light Mode / High Streak State
        DashboardPreviewState(
            streakDays = listOf(
                StreakDayState("S", progress = 1.0f, isCurrentDay = false),
                StreakDayState("M", progress = 1.0f, isCurrentDay = false),
                StreakDayState("Tu", progress = 0.0f, isCurrentDay = false),
                StreakDayState("W", progress = 1.0f, isCurrentDay = false),
                StreakDayState("Th", progress = 0.4f, isCurrentDay = true),
                StreakDayState("F", progress = 0.0f, isCurrentDay = false),
                StreakDayState("S", progress = 0.0f, isCurrentDay = false)
            ),
            historyLogs = listOf(
                DrunkCupHistory(amountMl = 250, count = 2),
                DrunkCupHistory(amountMl = 500, count = 1)
            ),
            currentIntake = 1000,
            targetIntake = 2500,
            streakCount = 365
        ),
        // State 2: Dark Mode / Complete Week Progress State
        DashboardPreviewState(
            streakDays = listOf(
                StreakDayState("S", progress = 1.0f, isCurrentDay = false),
                StreakDayState("M", progress = 1.0f, isCurrentDay = false),
                StreakDayState("Tu", progress = 1.0f, isCurrentDay = false),
                StreakDayState("W", progress = 1.0f, isCurrentDay = false),
                StreakDayState("Th", progress = 1.0f, isCurrentDay = false),
                StreakDayState("F", progress = 0.1f, isCurrentDay = true),
                StreakDayState("S", progress = 0.0f, isCurrentDay = false)
            ),
            historyLogs = listOf(
                DrunkCupHistory(amountMl = 250, count = 3),
                DrunkCupHistory(amountMl = 350, count = 1),
                DrunkCupHistory(amountMl = 750, count = 1)
            ),
            currentIntake = 1850,
            targetIntake = 3000,
            streakCount = 5
        )
    )
}