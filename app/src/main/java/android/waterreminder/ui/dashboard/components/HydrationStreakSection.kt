package android.waterreminder.ui.dashboard.components

import android.waterreminder.ui.core.components.DashboardSection
import android.waterreminder.ui.core.components.HydrationNode
import android.waterreminder.ui.core.components.LabelPill
import android.waterreminder.ui.core.components.StreakDayState
import android.waterreminder.ui.theme.ErtawyTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun HydrationStreakSection(
    streakDays: List<StreakDayState>,
    streakCountText: String, // e.g., "365 days hydrated!"
    modifier: Modifier = Modifier
) {
    DashboardSection(
        title = "Hydration Streak",
        modifier = modifier
    ) {
        // Main Container Card matching Figma geometric border frames
        Column(
            modifier = Modifier
                .width(342.dp)
                .height(134.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = MaterialTheme.shapes.medium // Ensure your theme sets cornerRadius = 15.dp
                )
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            // --- Component 1: Days-Hydrated Label Pill ---
            LabelPill(text = streakCountText)

            // --- Component 2: 7-Day Tracker Row Layout ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom // Keeps tracking text labels baseline-aligned
            ) {
                streakDays.take(7).forEach { day ->
                    HydrationNode(state = day)
                }
            }
        }
    }
}

@Preview(name = "Streak Full Container - Light Mode", showBackground = true)
@Composable
fun HydrationStreakLightPreview() {
    ErtawyTheme(darkTheme = false) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            HydrationStreakSection(
                streakCountText = "365 days hydrated!",
                streakDays = listOf(
                    StreakDayState("S", progress = 1.0f, isCurrentDay = false),
                    StreakDayState("M", progress = 1.0f, isCurrentDay = false),
                    StreakDayState("Tu", progress = 0.0f, isCurrentDay = false),
                    StreakDayState("W", progress = 1.0f, isCurrentDay = false),
                    StreakDayState("Th", progress = 0.4f, isCurrentDay = true),
                    StreakDayState("F", progress = 0.0f, isCurrentDay = false),
                    StreakDayState("S", progress = 0.0f, isCurrentDay = false)
                )
            )
        }
    }
}

@Preview(name = "Streak Full Container - Dark Mode", showBackground = true)
@Composable
fun HydrationStreakDarkModePreview() {
    ErtawyTheme(darkTheme = true) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            HydrationStreakSection(
                streakCountText = "5 days hydrated!",
                streakDays = listOf(
                    StreakDayState("S", progress = 1.0f, isCurrentDay = false),
                    StreakDayState("M", progress = 1.0f, isCurrentDay = false),
                    StreakDayState("Tu", progress = 1.0f, isCurrentDay = false),
                    StreakDayState("W", progress = 1.0f, isCurrentDay = false),
                    StreakDayState("Th", progress = 1.0f, isCurrentDay = false),
                    StreakDayState("F", progress = 0.1f, isCurrentDay = true),
                    StreakDayState("S", progress = 0.0f, isCurrentDay = false)
                )
            )
        }
    }
}