package android.waterreminder.ui.core.components

import android.waterreminder.R
import android.waterreminder.ui.dashboard.StreakDayState
import android.waterreminder.ui.dashboard.preview.HydrationNodeStateProvider
import android.waterreminder.ui.dashboard.preview.NodePreviewScenario
import android.waterreminder.ui.theme.ErtawyTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp

@Composable
fun HydrationNode(
    state: StreakDayState,
    isCurrentDay: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.wrapContentWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Circle Progress Node
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .then(
                    if (state.progress > 0f && state.progress < 1f) {
                        val waterLevelCutoff = (1f - state.progress).coerceIn(0f, 1f)

                        Modifier.background(
                            Brush.verticalGradient(
                                waterLevelCutoff to MaterialTheme.colorScheme.surface,
                                (waterLevelCutoff + 0.001f) to MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    } else if (state.progress >= 1f) {
                        Modifier.background(MaterialTheme.colorScheme.primaryContainer)
                    } else {
                        Modifier.background(MaterialTheme.colorScheme.surface)
                    }
                )
                .border(
                    width = if (isCurrentDay) 3.dp else 2.dp,
                    color = if (isCurrentDay) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.primary
                    },
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (state.progress >= 1f) {
                Text(
                    text = stringResource(R.string.dashboard_progress_goal_reached),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Node Text Label
        Text(
            text = state.dayLabel,
            style = MaterialTheme.typography.labelSmall,
            color = if (isCurrentDay) {
                MaterialTheme.colorScheme.onPrimaryContainer
            } else {
                MaterialTheme.colorScheme.primary
            },
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Preview(name = "Node - Light Mode", group = "Themes", showBackground = true)
@Preview(name = "Node - Dark Mode", group = "Themes", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HydrationNodeParameterPreview(
    @PreviewParameter(HydrationNodeStateProvider::class) scenario: NodePreviewScenario
) {

    ErtawyTheme {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp)
        ) {
            HydrationNode(
                state = scenario.state,
                isCurrentDay = scenario.isCurrentDay
            )
        }
    }
}