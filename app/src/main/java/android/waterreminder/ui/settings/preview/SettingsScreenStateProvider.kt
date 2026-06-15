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
            unit = "ml",
            isFasting = false,
            frequency = 60,
            theme = "System",
            language = "English",
            startTime = "07:00 AM",
            endTime = "09:00 PM",
            activeStartTime = "07:00 AM",
            activeEndTime = "09:00 PM",
            predefinedGoals = defaultGoalOptions,
            supportedUnits = supportedUnitsOptions,
            supportedFrequencies = supportedFrequencies
        ),

        // Scenario 2: Alternate Metric Setup (Fluid Ounces, Custom Guardrail Interval, Dark Theme)
        SettingsUiState(
            dailyGoalMl = 2500,
            unit = "fl oz",
            isFasting = false,
            frequency = 15,
            theme = "Dark",
            language = "German",
            startTime = "06:00 AM",
            endTime = "11:30 PM",
            activeStartTime = "06:00 AM",
            activeEndTime = "11:30 PM",
            predefinedGoals = defaultGoalOptions,
            supportedUnits = supportedUnitsOptions,
            supportedFrequencies = supportedFrequencies
        ),

        // Scenario 3: Ramadan/Fasting Operational Mode Window (Calculated Dynamic Active Hours)
        SettingsUiState(
            dailyGoalMl = 1800,
            unit = "ml",
            isFasting = true,
            frequency = 90,
            theme = "Light",
            language = "Arabic",
            startTime = "07:00 AM",
            endTime = "09:00 PM",
            activeStartTime = "06:45 PM",
            activeEndTime = "04:15 AM",
            predefinedGoals = defaultGoalOptions,
            supportedUnits = supportedUnitsOptions,
            supportedFrequencies = supportedFrequencies
        )
    ).asSequence()
}