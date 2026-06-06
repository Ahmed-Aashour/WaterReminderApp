package android.waterreminder.ui.core.components

import android.waterreminder.ui.dashboard.StreakDayState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun HydrationNode(
    state: StreakDayState,
    isCurrentDay: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.width(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // --- 1. Circle Progress Node ---
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
                    text = "✓",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // --- 2. Node Text Label ---
        Text(
            text = state.dayLabel,
            style = MaterialTheme.typography.labelSmall,
            color = if (isCurrentDay) {
                MaterialTheme.colorScheme.onPrimaryContainer
            } else {
                MaterialTheme.colorScheme.primary
            },
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(name = "Node Component - All States Breakdown", showBackground = true)
@Composable
fun HydrationNodeStatesPreview() {
    ErtawyTheme(darkTheme = false) {
        Box(modifier = Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                // State A: Past completed day with check mark
                HydrationNode(
                    state = StreakDayState(
                        dayLabel = "M",
                        progress = 1.0f,
                    ),
                    isCurrentDay = false,
                )

                // State B: Current active day with 40% partial water wave fill
                HydrationNode(
                    state = StreakDayState(
                        dayLabel = "Tu",
                        progress = 0.4f,
                    ),
                    isCurrentDay = true,
                )

                // State C: Future day with empty baseline state
                HydrationNode(
                    state = StreakDayState(
                        dayLabel = "W",
                        progress = 0.0f,
                    ),
                    isCurrentDay = false,
                )
            }
        }
    }
}