package android.waterreminder.data.entity

/**
 * Pure data-layer snapshot representing exactly what is persisted on disk.
 * Free of dynamic UI logic, presentation strings, or computed states.
 */
data class UserPreferences(
    val dailyGoalMl: Int,
    val unit: String,
    val areNotificationsEnabled: Boolean,
    val frequency: Int,
    val startTime: String,
    val endTime: String,
    val isFasting: Boolean,
    val city: String,
    val country: String,
    val theme: AppTheme,
    val language: String,
)