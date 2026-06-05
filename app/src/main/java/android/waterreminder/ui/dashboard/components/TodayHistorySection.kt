package android.waterreminder.ui.dashboard.components

import android.waterreminder.ui.core.components.DashboardSection
import android.waterreminder.ui.dashboard.preview.HistoryLogsProvider
import android.waterreminder.ui.theme.ErtawyTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Data representation matching the grouped preset structure identified in the Figma spec.
 */
data class DrunkCupHistory(
    val amountMl: Int,
    val count: Int
)

@Composable
fun TodayHistorySection(
    historyItems: List<DrunkCupHistory>,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    DashboardSection(
        title = "Today’s Drink History",
        modifier = modifier
    ) {
        // --- Core Container Card Frame (342dp x 94dp) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(94.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(15.dp)
                )
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(15.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (historyItems.isEmpty()) {
                // Empty State Handler View
                Text(
                    text = "No Cups Drank yet!",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                )
            } else {
                // Scrollable Content Row for active logs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .horizontalScroll(scrollState),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    historyItems.forEach { item ->
                        HistoryCupChip(item = item)
                    }
                }
            }
        }
    }
}

/**
 * Split-box item card layout rendering individual metrics.
 */
@Composable
private fun HistoryCupChip(
    item: DrunkCupHistory,
    modifier: Modifier = Modifier
) {
    val shapeToken = RoundedCornerShape(8.dp)

    Row(
        modifier = modifier
            .width(80.dp)
            .height(55.dp)
            .border(2.dp, MaterialTheme.colorScheme.primary, shapeToken)
            .clip(shapeToken),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left Segment Pane: Multiplier text (e.g., 5x)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${item.count}×",
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
            )
        }

        // Right Segment Pane: Shaded container capacity text block
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(
                    width = (1.5).dp,
                    color = MaterialTheme.colorScheme.primary
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${item.amountMl}\nml",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )
            )
        }
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Composable
fun TodayHistoryActivePreview(
    @PreviewParameter(HistoryLogsProvider ::class) mockHistory: List<DrunkCupHistory>
) {
    ErtawyTheme(darkTheme = false) {
        Box(modifier = Modifier.padding(16.dp)) {
            TodayHistorySection(historyItems = mockHistory)
        }
    }
}

@Preview(name = "Dark Mode", showBackground = true)
@Composable
fun TodayHistoryDarkModePreview(
    @PreviewParameter(HistoryLogsProvider ::class) mockHistory: List<DrunkCupHistory>
) {
    ErtawyTheme(darkTheme = true) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            TodayHistorySection(historyItems = mockHistory)
        }
    }
}