package android.waterreminder.ui.settings

/**
 * UI-specific state snapshot representing everything the screen needs to display.
 */
data class SettingsUiState(
    val dailyGoalMl: Int,
    val measurementUnit: String,
    val isFasting: Boolean,
    val notificationInterval: Int,
    val theme: String,
    val language: String,
    val savedStartHour: String,
    val savedEndHour: String,
    val activeStartHour: String, // 🌟 Dynamically computed for UI layout visibility
    val activeEndHour: String,   // 🌟 Dynamically computed for UI layout visibility
    val predefinedGoalOptions: List<GoalOptionUiModel> = emptyList(), // Co-located for easy extraction
    val supportedUnits: List<String> = emptyList()
)

/**
 * Pre-calculated packaging model for rendering selection choices inside the dialogue.
 */
data class GoalOptionUiModel(
    val amountMl: Int,
    val displayLabelMl: String,
    val displayLabelOz: String
)