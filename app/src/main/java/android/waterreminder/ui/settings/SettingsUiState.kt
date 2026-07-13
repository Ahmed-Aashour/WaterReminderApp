package android.waterreminder.ui.settings

import android.waterreminder.data.entity.AppLanguage
import android.waterreminder.data.entity.AppTheme
import android.waterreminder.data.entity.AppUnit
import java.time.LocalTime

/**
 * UI-specific state snapshot representing everything the [SettingsScreen]
 * needs to display.
 */
data class SettingsUiState(
    val dailyGoalMl: Int,
    val predefinedGoals: List<Int> = emptyList(),
    val unit: AppUnit,

    val areNotificationsEnabled: Boolean = true,
    val frequency: Int,
    val supportedFrequencies: List<Int>,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val activeStartTime: LocalTime,
    val activeEndTime: LocalTime,
    val isFasting: Boolean,

    val theme: AppTheme,
    val language: AppLanguage,
)

sealed interface ActiveSettingsDialog {
    data object None : ActiveSettingsDialog
    data object DailyGoal : ActiveSettingsDialog
    data object Unit : ActiveSettingsDialog
    data object Frequency : ActiveSettingsDialog
    data object Period : ActiveSettingsDialog
    data object Theme : ActiveSettingsDialog
    data object Language : ActiveSettingsDialog
}