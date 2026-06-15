package android.waterreminder.ui.settings.preview

import android.waterreminder.ui.settings.SettingsUiState
import android.waterreminder.ui.settings.toFrequencyUiModels
import android.waterreminder.ui.settings.toGoalUiModels
import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class SettingsScreenStateProvider : PreviewParameterProvider<SettingsUiState> {

    private val defaultGoalOptions = listOf(2000, 2250, 2500, 2750, 3000).toGoalUiModels(0.033814)
    private val supportedUnitsOptions = listOf("ml", "fl oz")
    private val supportedFrequencies = listOf(15, 30, 60, 90, 120, 180).toFrequencyUiModels()

    override val values: Sequence<SettingsUiState> = listOf(
        // Scenario 1: Clean Baseline System Configuration (Out-of-the-box Default State)
        SettingsUiState(
            dailyGoalMl = 2000,
            predefinedGoals = defaultGoalOptions,
            unit = "ml",
            supportedUnits = supportedUnitsOptions,
            areNotificationsEnabled = true,
            frequency = 60,
            supportedFrequencies = supportedFrequencies,
            startTime = "07:00 AM",
            endTime = "09:00 PM",
            activeStartTime = "07:00 AM",
            activeEndTime = "09:00 PM",
            isFasting = false,
            theme = "System",
            language = "English",
        ),

        // Scenario 2: Alternate Metric Setup (Fluid Ounces, Custom Guardrail Interval, Dark Theme)
        SettingsUiState(
            dailyGoalMl = 2500,
            predefinedGoals = defaultGoalOptions,
            unit = "fl oz",
            supportedUnits = supportedUnitsOptions,
            areNotificationsEnabled = true,
            frequency = 15,
            supportedFrequencies = supportedFrequencies,
            startTime = "06:00 AM",
            endTime = "11:30 PM",
            activeStartTime = "06:00 AM",
            activeEndTime = "11:30 PM",
            isFasting = false,
            theme = "Dark",
            language = "German",
        ),

        // Scenario 3: Ramadan/Fasting Operational Mode Window (Calculated Dynamic Active Hours)
        SettingsUiState(
            dailyGoalMl = 1800,
            predefinedGoals = defaultGoalOptions,
            unit = "ml",
            supportedUnits = supportedUnitsOptions,
            areNotificationsEnabled = false,
            frequency = 90,
            supportedFrequencies = supportedFrequencies,
            startTime = "07:00 AM",
            endTime = "09:00 PM",
            activeStartTime = "06:45 PM",
            activeEndTime = "04:15 AM",
            isFasting = true,
            theme = "Light",
            language = "Arabic",
        )
    ).asSequence()
}