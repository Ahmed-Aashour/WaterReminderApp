package android.waterreminder.data.store

/**
 * 🎛️ Central App Settings Configuration Control Panel
 * Modify these centralized fields to dynamically shift global app rules instantly!
 */
object SettingsConfig {
    // Default Fallback Configurations
    const val DEFAULT_DAILY_GOAL_ML = 2000
    const val DEFAULT_MEASUREMENT_UNIT = "ml"
    const val DEFAULT_IS_FASTING = false
    const val DEFAULT_NOTIFICATION_INTERVAL_MIN = 60
    const val DEFAULT_THEME = "System"
    const val DEFAULT_LANGUAGE = "English"
    const val DEFAULT_START_TIME = "07:00 AM"
    const val DEFAULT_END_TIME = "09:00 PM"

    // Runtime Guardrail Configurations
    const val MIN_NOTIFICATION_INTERVAL_MIN = 15
    const val MAX_NOTIFICATION_INTERVAL_MIN = 180

    // Supported Validation Collections
    val SUPPORTED_THEMES = listOf("Light", "Dark", "System")
    val SUPPORTED_LANGUAGES = listOf("English", "Arabic", "German", "French", "Italian")
    val SUPPORTED_UNITS = listOf("ml", "oz")
}