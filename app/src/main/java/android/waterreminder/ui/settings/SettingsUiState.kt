package android.waterreminder.ui.settings

import android.waterreminder.data.entity.AppLanguage
import android.waterreminder.data.entity.AppTheme
import android.waterreminder.data.entity.AppUnit

/**
 * UI-specific state snapshot representing everything the [SettingsScreen]
 * needs to display.
 */
data class SettingsUiState(
    val dailyGoalMl: Int,
    val predefinedGoals: List<GoalOptionUiModel> = emptyList(),
    val unit: AppUnit,

    val areNotificationsEnabled: Boolean = true,
    val frequency: Int,
    val supportedFrequencies: List<Int>,
    val startTime: String,
    val endTime: String,
    val activeStartTime: String,
    val activeEndTime: String,
    val isFasting: Boolean,

    val theme: AppTheme,
    val language: AppLanguage,
)

data class GoalOptionUiModel(
    val amountMl: Int,
    val displayLabelMl: String,
    val displayLabelOz: String
)

fun List<Int>.toGoalUiModels(mlToOzFactor: Double): List<GoalOptionUiModel> {
    return this.map { ml ->
        GoalOptionUiModel(
            amountMl = ml,
            displayLabelMl = "$ml ml",
            displayLabelOz = "${(ml * mlToOzFactor).toInt()} fl oz"
        )
    }
}