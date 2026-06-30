package android.waterreminder.ui.dashboard.components

import android.waterreminder.R
import android.waterreminder.ui.core.components.DashboardSection
import android.waterreminder.ui.core.components.HydrationNode
import android.waterreminder.ui.core.components.LabelPill
import android.waterreminder.ui.dashboard.StreakSectionState
import android.waterreminder.ui.dashboard.preview.StreakSectionProvider
import android.waterreminder.ui.theme.ErtawyTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp

@Composable
fun HydrationStreakSection(
    state: StreakSectionState,
    modifier: Modifier = Modifier
) {
    DashboardSection(
        title = stringResource(R.string.dashboard_section_streak_title),
        modifier = modifier
    ) {
        // Main Container Card matching Figma geometric border frames
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = MaterialTheme.shapes.medium
                )
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Days-Hydrated Label Pill
            LabelPill(
                text = pluralStringResource(
                    id = R.plurals.dashboard_streak_count_format,
                    count = state.count,
                    state.count
                )
            )

            // 7-Day Tracker Row Layout
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                state.days.take(7).forEachIndexed { index, day ->
                    HydrationNode(
                        state = day,
                        isCurrentDay = (index == state.dayIndex)
                    )
                }
            }
        }
    }
}

@Preview(name = "Streak Full Container - Light Mode", showBackground = true)
@Preview(name = "Streak Full Container - Dark Mode", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HydrationStreakDarkModePreview(
    @PreviewParameter(StreakSectionProvider::class) mockState: StreakSectionState
) {
    ErtawyTheme {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            HydrationStreakSection(state = mockState)
        }
    }
}