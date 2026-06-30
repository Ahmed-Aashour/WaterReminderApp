package android.waterreminder.ui.dashboard.preview

import android.waterreminder.data.entity.AppUnit
import android.waterreminder.ui.dashboard.DashboardState
import android.waterreminder.ui.dashboard.DisplayIntakeState
import android.waterreminder.ui.dashboard.DrinkButtonsState
import android.waterreminder.ui.dashboard.DrunkCupHistory
import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class DashboardScreenStateProvider : PreviewParameterProvider<DashboardState> {
    override val values: Sequence<DashboardState> = listOf(
        // 🚀 Scenario 0: Empty History Log State (Fresh Day Start)
        DashboardState(
            progressDisplay = DisplayIntakeState(
                currentLabel = "0",
                targetLabel = "2500",
                unit = AppUnit.ML,
                progressFraction = 0.0f,
                progressPercentage = 0
            ),
            historyLogs = emptyList(), // Testing the new action-driven empty state UX
            drinkButtons = DrinkButtonsState(
                unit = AppUnit.ML,
                presetAmountsMl = listOf(250, 350, 500)
            ),
            streakSection = MockStreakData.highStreakMidWeek // Preserving an active streak context
        ),
        // Scenario 1: High Streak (365 Days) / Mid-Week Progression (mL setup)
        DashboardState(
            progressDisplay = DisplayIntakeState(
                currentLabel = "1000",
                targetLabel = "2500",
                unit = AppUnit.ML,
                progressFraction = 0.40f,
                progressPercentage = 40
            ),
            historyLogs = listOf(
                DrunkCupHistory(id = 1L, amountMl = 250, timeLogged = "07:00 AM"),
                DrunkCupHistory(id = 2L, amountMl = 500, timeLogged = "08:00 AM"),
                DrunkCupHistory(id = 3L, amountMl = 250, timeLogged = "09:50 AM")
            ),
            drinkButtons = DrinkButtonsState(
                unit = AppUnit.ML,
                presetAmountsMl = listOf(250, 350, 500)
            ),
            streakSection = MockStreakData.highStreakMidWeek
        ),
        // Scenario 2: Standard Week Progress (5 Days) / Friday Verification (oz setup)
        DashboardState(
            progressDisplay = DisplayIntakeState(
                currentLabel = "63",
                targetLabel = "101",
                unit = AppUnit.OZ,
                progressFraction = 0.62f,
                progressPercentage = 62
            ),
            historyLogs = listOf(
                DrunkCupHistory(id = 1L, amountMl = 250, timeLogged = "07:00 AM"),
                DrunkCupHistory(id = 2L, amountMl = 250, timeLogged = "07:00 AM"),
                DrunkCupHistory(id = 3L, amountMl = 250, timeLogged = "07:00 AM"),
                DrunkCupHistory(id = 4L, amountMl = 350, timeLogged = "07:00 AM"),
                DrunkCupHistory(id = 5L, amountMl = 750, timeLogged = "07:00 AM")
            ),
            drinkButtons = DrinkButtonsState(
                unit = AppUnit.OZ,
                presetAmountsMl = listOf(250, 350, 500)
            ),
            streakSection = MockStreakData.fiveDayFridayProgress
        )
    ).asSequence()
}