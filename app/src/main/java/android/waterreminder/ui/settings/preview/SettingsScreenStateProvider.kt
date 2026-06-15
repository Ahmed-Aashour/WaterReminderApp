package android.waterreminder.ui.settings.preview

import android.waterreminder.ui.settings.GoalOptionUiModel
import android.waterreminder.ui.settings.SettingsUiState
import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class SettingsScreenStateProvider : PreviewParameterProvider<SettingsUiState> {

    private val defaultGoalOptions = listOf(2000, 2250, 2500, 2750, 3000).map { ml ->
        GoalOptionUiModel(
            amountMl = ml,
            displayLabelMl = "$ml ml",
            displayLabelOz = "${(ml * 0.033814).toInt()} fl oz"
        )
    }

    override val values: Sequence<SettingsUiState> = listOf(
        // Scenario 1: Clean Baseline System Configuration (Out-of-the-box Default State)
        SettingsUiState(
            dailyGoalMl = 2000,
            measurementUnit = "ml",
            isFasting = false,
            notificationInterval = 60,
            theme = "System",
            language = "English",
            savedStartHour = "07:00 AM",
            savedEndHour = "09:00 PM",
            activeStartHour = "07:00 AM",
            activeEndHour = "09:00 PM",
            predefinedGoalOptions = defaultGoalOptions
        ),

        // Scenario 2: Alternate Metric Setup (Fluid Ounces, Custom Guardrail Interval, Dark Theme)
        SettingsUiState(
            dailyGoalMl = 2500,
            measurementUnit = "oz",
            isFasting = false,
            notificationInterval = 15,
            theme = "Dark",
            language = "German",
            savedStartHour = "06:00 AM",
            savedEndHour = "11:30 PM",
            activeStartHour = "06:00 AM",
            activeEndHour = "11:30 PM",
            predefinedGoalOptions = defaultGoalOptions
        ),

        // Scenario 3: Ramadan/Fasting Operational Mode Window (Calculated Dynamic Active Hours)
        SettingsUiState(
            dailyGoalMl = 1800,
            measurementUnit = "ml",
            isFasting = true,
            notificationInterval = 45,
            theme = "Light",
            language = "Arabic",
            savedStartHour = "07:00 AM",
            savedEndHour = "09:00 PM",
            activeStartHour = "06:45 PM",
            activeEndHour = "04:15 AM",
            predefinedGoalOptions = defaultGoalOptions
        )
    ).asSequence()
}