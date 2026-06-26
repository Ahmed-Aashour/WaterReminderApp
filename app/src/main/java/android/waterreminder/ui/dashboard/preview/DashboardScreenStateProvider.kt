package android.waterreminder.ui.dashboard.preview

import android.waterreminder.ui.dashboard.DashboardState
import android.waterreminder.ui.dashboard.DrunkCupHistory
import androidx.compose.ui.tooling.preview.PreviewParameterProvider


class DashboardScreenStateProvider : PreviewParameterProvider<DashboardState> {
    override val values: Sequence<DashboardState> = listOf(
        // Scenario 1: High Streak (365 Days) / Mid-Week Progression
        DashboardState(
            currentIntake = 1000,
            targetIntake = 2500,
            historyLogs = listOf(
                DrunkCupHistory(id = 1L, amountMl = 250, timeLogged = "07:00 AM"),
                DrunkCupHistory(id = 2L, amountMl = 500, timeLogged = "08:00 AM"),
                DrunkCupHistory(id = 3L, amountMl = 250, timeLogged = "09:50 AM")
            ),
            streakSection = MockStreakData.highStreakMidWeek
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
            streakSection = MockStreakData.fiveDayFridayProgress
        )
    ).asSequence()
}