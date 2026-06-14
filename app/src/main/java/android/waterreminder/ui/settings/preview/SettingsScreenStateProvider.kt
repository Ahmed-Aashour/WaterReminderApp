package android.waterreminder.ui.settings.preview

import android.waterreminder.data.store.SettingsState
import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class SettingsScreenStateProvider : PreviewParameterProvider<SettingsState> {
    override val values: Sequence<SettingsState> = sequenceOf(
        // Scenario 1: Clean Baseline System Configuration (Out-of-the-box Default State)
        SettingsState(
            dailyGoalMl = 2000,
            measurementUnit = "ml",
            isFasting = false,
            notificationInterval = 60,
            theme = "System",
            language = "English",
            savedStartHour = "07:00 AM",
            savedEndHour = "09:00 PM",
            activeStartHour = "07:00 AM",
            activeEndHour = "09:00 PM"
        ),

        // Scenario 2: Alternate Metric Setup (Fluid Ounces, Custom Guardrail Interval, Dark Theme)
        SettingsState(
            dailyGoalMl = 2500,
            measurementUnit = "oz",
            isFasting = false,
            notificationInterval = 15,
            theme = "Dark",
            language = "German",
            savedStartHour = "06:00 AM",
            savedEndHour = "11:30 PM",
            activeStartHour = "06:00 AM",
            activeEndHour = "11:30 PM"
        ),

        // Scenario 3: Ramadan/Fasting Operational Mode Window (Calculated Dynamic Active Hours)
        SettingsState(
            dailyGoalMl = 1800,
            measurementUnit = "ml",
            isFasting = true,
            notificationInterval = 45,
            theme = "Light",
            language = "Arabic",
            savedStartHour = "07:00 AM",
            savedEndHour = "09:00 PM",
            activeStartHour = "06:45 PM",
            activeEndHour = "04:15 AM"
        )
    )
}