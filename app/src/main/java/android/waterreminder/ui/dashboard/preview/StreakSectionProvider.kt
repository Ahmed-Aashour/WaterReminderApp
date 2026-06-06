package android.waterreminder.ui.dashboard.preview

import android.waterreminder.ui.dashboard.StreakSectionState
import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class StreakSectionProvider : PreviewParameterProvider<StreakSectionState> {
    override val values: Sequence<StreakSectionState> = sequenceOf(
        MockStreakData.highStreakMidWeek,
        MockStreakData.perfectWeekLockedIn,
    )
}