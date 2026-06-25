package android.waterreminder.ui.settings.preview

import android.waterreminder.data.entity.AppLanguage
import android.waterreminder.data.entity.AppTheme
import android.waterreminder.data.entity.AppUnit
import android.waterreminder.ui.settings.SettingsUiState
import android.waterreminder.ui.settings.toGoalUiModels
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import java.time.LocalTime

class SettingsScreenStateProvider : PreviewParameterProvider<SettingsUiState> {

    private val defaultGoalOptions = listOf(2000, 2250, 2500, 2750, 3000).toGoalUiModels(0.033814)
    private val supportedFrequencies = listOf(15, 30, 60, 90, 120, 180)

    override val values: Sequence<SettingsUiState> = listOf(
        // Scenario 1: Clean Baseline System Configuration (Out-of-the-box Default State)
        SettingsUiState(
            dailyGoalMl = 2000,
            predefinedGoals = defaultGoalOptions,
            unit = AppUnit.ML,
            areNotificationsEnabled = true,
            frequency = 60,
            supportedFrequencies = supportedFrequencies,
            startTime = LocalTime.of(7, 0),        // 07:00
            endTime = LocalTime.of(21, 0),         // 21:00
            activeStartTime = LocalTime.of(7, 0),  // 07:00
            activeEndTime = LocalTime.of(21, 0),   // 21:00
            isFasting = false,
            theme = AppTheme.SYSTEM,
            language = AppLanguage.ENGLISH,
        ),

        // Scenario 2: Alternate Metric Setup (Fluid Ounces, Custom Guardrail Interval, Dark Theme)
        SettingsUiState(
            dailyGoalMl = 2500,
            predefinedGoals = defaultGoalOptions,
            unit = AppUnit.OZ,
            areNotificationsEnabled = true,
            frequency = 15,
            supportedFrequencies = supportedFrequencies,
            startTime = LocalTime.of(6, 0),         // 06:00
            endTime = LocalTime.of(23, 30),        // 23:30
            activeStartTime = LocalTime.of(6, 0),   // 06:00
            activeEndTime = LocalTime.of(23, 30),  // 23:30
            isFasting = false,
            theme = AppTheme.DARK,
            language = AppLanguage.GERMAN,
        ),

        // Scenario 3: Ramadan/Fasting Operational Mode Window (Calculated Dynamic Active Hours)
        SettingsUiState(
            dailyGoalMl = 1800,
            predefinedGoals = defaultGoalOptions,
            unit = AppUnit.ML,
            areNotificationsEnabled = false,
            frequency = 90,
            supportedFrequencies = supportedFrequencies,
            startTime = LocalTime.of(7, 0),         // 07:00
            endTime = LocalTime.of(21, 0),         // 21:00
            activeStartTime = LocalTime.of(18, 45), // 18:45 (Maghrib)
            activeEndTime = LocalTime.of(4, 15),    // 04:15 (Fajr tomorrow)
            isFasting = true,
            theme = AppTheme.LIGHT,
            language = AppLanguage.ARABIC,
        )
    ).asSequence()
}