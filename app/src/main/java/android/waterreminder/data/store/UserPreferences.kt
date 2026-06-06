package android.waterreminder.data.store

/**
 * Read-only state snapshot model representing the application preferences framework.
 */
data class UserPreferences(
    val dailyGoalMl: Int,
    val measurementUnit: String,
    val isFasting: Boolean,
    val notificationInterval: Int,
    val theme: String,
    val language: String,
    val savedStartHour: String,
    val savedEndHour: String,
    val activeStartHour: String,
    val activeEndHour: String
)