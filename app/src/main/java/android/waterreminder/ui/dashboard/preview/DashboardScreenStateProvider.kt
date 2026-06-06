package android.waterreminder.ui.dashboard.preview

import android.waterreminder.ui.dashboard.DashboardState
import android.waterreminder.ui.dashboard.DrunkCupHistory
import android.waterreminder.ui.dashboard.StreakDayState
import android.waterreminder.ui.dashboard.StreakSectionState
import androidx.compose.ui.tooling.preview.PreviewParameterProvider


class DashboardScreenStateProvider : PreviewParameterProvider<DashboardState> {
    override val values: Sequence<DashboardState> = sequenceOf(
        // Scenario 1: High Streak (365 Days) / Mid-Week Progression
        DashboardState(
            currentIntake = 1000,
            targetIntake = 2500,
            historyLogs = listOf(
                DrunkCupHistory(id = 1L, amountMl = 250, timeLogged = "07:00 AM"),
                DrunkCupHistory(id = 2L, amountMl = 500, timeLogged = "08:00 AM"),
                DrunkCupHistory(id = 3L, amountMl = 250, timeLogged = "09:50 AM")
            ),
            streakSection = StreakSectionState(
                count = 365,
                dayIndex = 4, // Thursday
                days = listOf(
                    StreakDayState("S", progress = 1.0f),
                    StreakDayState("M", progress = 1.0f),
                    StreakDayState("Tu", progress = 0.0f),
                    StreakDayState("W", progress = 1.0f),
                    StreakDayState("Th", progress = 0.4f), // Today
                    StreakDayState("F", progress = 0.0f),
                    StreakDayState("S", progress = 0.0f)
                )
            )
        ),
        // Scenario 2: Standard Week Progress (5 Days) / Friday Verification
        DashboardState(
            currentIntake = 1850,
            targetIntake = 3000,
            historyLogs = listOf(
                DrunkCupHistory(id = 1L, amountMl = 250, timeLogged = "07:00 AM"),
                DrunkCupHistory(id = 2L, amountMl = 250, timeLogged = "07:00 AM"),
                DrunkCupHistory(id = 3L, amountMl = 250, timeLogged = "07:00 AM"),
                DrunkCupHistory(id = 4L, amountMl = 350, timeLogged = "07:00 AM"),
                DrunkCupHistory(id = 5L, amountMl = 750, timeLogged = "07:00 AM")
            ),
            streakSection = StreakSectionState(
                count = 5,
                dayIndex = 5, // Friday
                days = listOf(
                    StreakDayState("S", progress = 1.0f),
                    StreakDayState("M", progress = 1.0f),
                    StreakDayState("Tu", progress = 1.0f),
                    StreakDayState("W", progress = 1.0f),
                    StreakDayState("Th", progress = 1.0f),
                    StreakDayState("F", progress = 0.1f), // Today
                    StreakDayState("S", progress = 0.0f)
                )
            )
        )
    )
}