package android.waterreminder.data.store

/**
 * Pure data-layer snapshot representing exactly what is persisted on disk.
 * Free of dynamic UI logic, presentation strings, or computed states.
 */
data class UserPreferences(
    val dailyGoalMl: Int,
    val measurementUnit: String,
    val isFasting: Boolean,
    val notificationInterval: Int,
    val theme: String,
    val language: String,
    val savedStartHour: String,
    val savedEndHour: String
)