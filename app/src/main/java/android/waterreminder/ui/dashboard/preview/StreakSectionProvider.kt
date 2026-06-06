package android.waterreminder.ui.dashboard.preview

import android.waterreminder.ui.dashboard.StreakDayState
import android.waterreminder.ui.dashboard.StreakSectionState
import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class StreakSectionProvider : PreviewParameterProvider<StreakSectionState> {
    override val values: Sequence<StreakSectionState> = sequenceOf(
        // Scenario A: Standard Mid-Week Progress
        StreakSectionState(
            count = 365,
            dayIndex = 4,
            days = listOf(
                StreakDayState("S", progress = 1.0f),
                StreakDayState("M", progress = 1.0f),
                StreakDayState("Tu", progress = 0.0f),
                StreakDayState("W", progress = 1.0f),
                StreakDayState("Th", progress = 0.4f),
                StreakDayState("F", progress = 0.0f),
                StreakDayState("S", progress = 0.0f)
            )
        ),
        // Scenario B: Perfect Week Streak Locked In!
        StreakSectionState(
            count = 7,
            dayIndex = 6,
            days = listOf(
                StreakDayState("S", progress = 1.0f),
                StreakDayState("M", progress = 1.0f),
                StreakDayState("Tu", progress = 1.0f),
                StreakDayState("W", progress = 1.0f),
                StreakDayState("Th", progress = 1.0f),
                StreakDayState("F", progress = 1.0f),
                StreakDayState("S", progress = 1.0f)
            )
        ),
    )
}