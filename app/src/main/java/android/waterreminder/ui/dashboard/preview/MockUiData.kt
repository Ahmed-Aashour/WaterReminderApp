package android.waterreminder.ui.dashboard.preview

import android.waterreminder.ui.dashboard.StreakDayState
import android.waterreminder.ui.dashboard.StreakSectionState

/**
 * Shared test datasets to eliminate duplication across Compose Previews.
 */
internal object MockStreakData {
    val highStreakMidWeek = StreakSectionState(
        count = 365,
        dayIndex = 4, // Thursday
        days = listOf(
            StreakDayState("S", progress = 1.0f),
            StreakDayState("M", progress = 1.0f),
            StreakDayState("Tu", progress = 0.0f),
            StreakDayState("W", progress = 1.0f),
            StreakDayState("Th", progress = 0.4f), // Today
            StreakDayState("F", progress = 0.0f),
            StreakDayState("S", progress = 0.0f)
        )
    )

    val fiveDayFridayProgress = StreakSectionState(
        count = 5,
        dayIndex = 5, // Friday
        days = listOf(
            StreakDayState("S", progress = 1.0f),
            StreakDayState("M", progress = 1.0f),
            StreakDayState("Tu", progress = 1.0f),
            StreakDayState("W", progress = 1.0f),
            StreakDayState("Th", progress = 1.0f),
            StreakDayState("F", progress = 0.1f), // Today
            StreakDayState("S", progress = 0.0f)
        )
    )

    val perfectWeekLockedIn = StreakSectionState(
        count = 7,
        dayIndex = 6, // Saturday
        days = listOf(
            StreakDayState("S", progress = 1.0f),
            StreakDayState("M", progress = 1.0f),
            StreakDayState("Tu", progress = 1.0f),
            StreakDayState("W", progress = 1.0f),
            StreakDayState("Th", progress = 1.0f),
            StreakDayState("F", progress = 1.0f),
            StreakDayState("S", progress = 1.0f)
        )
    )
}