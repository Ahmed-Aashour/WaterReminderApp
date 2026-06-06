package android.waterreminder.ui.dashboard.preview

import android.waterreminder.ui.dashboard.DashboardState
import android.waterreminder.ui.dashboard.DrunkCupHistory
import android.waterreminder.ui.dashboard.StreakDayState
import androidx.compose.ui.tooling.preview.PreviewParameterProvider


class DashboardScreenStateProvider : PreviewParameterProvider<DashboardState> {
    override val values: Sequence<DashboardState> = sequenceOf(
        // State 1: Light Mode / High Streak State
        DashboardState(
            streakDays = listOf(
                StreakDayState("S", progress = 1.0f),
                StreakDayState("M", progress = 1.0f),
                StreakDayState("Tu", progress = 0.0f),
                StreakDayState("W", progress = 1.0f),
                StreakDayState("Th", progress = 0.4f),
                StreakDayState("F", progress = 0.0f),
                StreakDayState("S", progress = 0.0f)
            ),
            historyLogs = listOf(
                DrunkCupHistory(id = 1, amountMl = 250, timeLogged = "07:00 AM"),
                DrunkCupHistory(id = 2, amountMl = 500, timeLogged = "08:00 AM"),
                DrunkCupHistory(id = 3, amountMl = 250, timeLogged = "09:50 AM"),
            ),
            currentIntake = 1000,
            targetIntake = 2500,
            streakCount = 365,
            currentDayIndex = 4
        ),
        // State 2: Dark Mode / Complete Week Progress State
        DashboardState(
            streakDays = listOf(
                StreakDayState("S", progress = 1.0f),
                StreakDayState("M", progress = 1.0f),
                StreakDayState("Tu", progress = 1.0f),
                StreakDayState("W", progress = 1.0f),
                StreakDayState("Th", progress = 1.0f),
                StreakDayState("F", progress = 0.1f),
                StreakDayState("S", progress = 0.0f)
            ),
            historyLogs = listOf(
                DrunkCupHistory(id = 1, amountMl = 250, timeLogged = "07:00 AM"),
                DrunkCupHistory(id = 2, amountMl = 250, timeLogged = "07:00 AM"),
                DrunkCupHistory(id = 3, amountMl = 250, timeLogged = "07:00 AM"),
                DrunkCupHistory(id = 4, amountMl = 350, timeLogged = "07:00 AM"),
                DrunkCupHistory(id = 5, amountMl = 750, timeLogged = "07:00 AM")
            ),
            currentIntake = 1850,
            targetIntake = 3000,
            streakCount = 5,
            currentDayIndex = 5
        )
    )
}