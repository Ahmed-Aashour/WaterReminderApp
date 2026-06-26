package android.waterreminder.data.entity

import java.time.LocalTime

/**
 * Pure data-layer snapshot representing exactly what is persisted on disk.
 * Free of dynamic UI logic, presentation strings, or computed states.
 */
data class UserPreferences(
    val dailyGoalMl: Int,
    val unit: AppUnit,
    val areNotificationsEnabled: Boolean,
    val frequency: Int,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val isFasting: Boolean,
    val city: String,
    val country: String,
    val theme: AppTheme,
    val language: AppLanguage,
)