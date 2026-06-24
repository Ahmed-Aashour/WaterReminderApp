package android.waterreminder.ui.settings

/**
 * UI-specific state snapshot representing everything the [SettingsScreen]
 * needs to display.
 */
data class SettingsUiState(
    val dailyGoalMl: Int,
    val predefinedGoals: List<GoalOptionUiModel> = emptyList(),
    val unit: String,
    val supportedUnits: List<String> = emptyList(),

    val areNotificationsEnabled: Boolean = true,
    val frequency: Int,
    val supportedFrequencies: List<FrequencyOptionUiModel> = emptyList(),
    val startTime: String,
    val endTime: String,
    val activeStartTime: String,
    val activeEndTime: String,
    val isFasting: Boolean,

    val theme: String,
    val supportedThemes: List<String> = emptyList(),
    val language: String,
    val supportedLanguages: List<String> = emptyList(),
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

data class FrequencyOptionUiModel(
    val minutes: Int,
    val displayLabel: String
)

fun List<Int>.toFrequencyUiModels(): List<FrequencyOptionUiModel> {
    return this.map { mins ->
        val label = when {
            mins < 60 -> "Every $mins min"
            mins == 60 -> "Every 1 hour"
            mins % 60 == 0 -> "Every ${mins / 60} hours"
            else -> "Every ${mins / 60.0} hours" // Handles 90 mins -> 1.5 hours flawlessly
        }
        FrequencyOptionUiModel(minutes = mins, displayLabel = label)
    }
}