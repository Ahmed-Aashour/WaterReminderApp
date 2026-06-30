package android.waterreminder.ui.dashboard.components

import android.content.res.Configuration
import android.waterreminder.R
import android.waterreminder.data.entity.AppUnit
import android.waterreminder.ui.core.components.DashboardSection
import android.waterreminder.ui.dashboard.DrunkCupHistory
import android.waterreminder.ui.dashboard.preview.HistoryLogsProvider
import android.waterreminder.ui.theme.ErtawyTheme
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TodayHistorySection(
    historyItems: List<DrunkCupHistory>,
    currentUnit: AppUnit,
    onDeleteLog: (DrunkCupHistory) -> Unit,
    modifier: Modifier = Modifier
) {
    DashboardSection(
        title = stringResource(R.string.dashboard_section_history_title),
        modifier = modifier
    ) {
        if (historyItems.isEmpty()) {
            // Empty State Box
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .border(
                        width = 1.5.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(vertical = 20.dp, horizontal = 16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.dashboard_history_empty_state_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stringResource(R.string.dashboard_history_empty_state_subtitle),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            // Clean Vertical Layout Stack
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                historyItems.forEach { item ->
                    DismissibleHistoryCupChip(
                        item = item,
                        currentUnit = currentUnit,
                        onDismissed = { onDeleteLog(item) }
                    )
                }
            }
        }
    }
}

/**
 * A wrapper container that handles swipe physics, threshold states, and background colors.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DismissibleHistoryCupChip(
    item: DrunkCupHistory,
    currentUnit: AppUnit,
    onDismissed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val itemShape = RoundedCornerShape(12.dp)
    val dismissState = rememberSwipeToDismissBoxState()
    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
            onDismissed()
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier.clip(itemShape),
        backgroundContent = {
            val isDismissing = dismissState.targetValue == SwipeToDismissBoxValue.EndToStart
            val backgroundColor by animateColorAsState(
                targetValue = if (isDismissing) MaterialTheme.colorScheme.error else Color.Transparent,
                label = "DeleteBackgroundAnimation"
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
                    .padding(end = 12.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.dashboard_accessibility_delete_log),
                    tint = if (isDismissing) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.error
                )
            }
        }
    ) {
        // The foreground content remains your original static design component
        HistoryCupChip(item = item, currentUnit = currentUnit, shape = itemShape)
    }
}

/**
 * Split-box item card layout rendering individual metrics.
 */
@Composable
private fun HistoryCupChip(
    item: DrunkCupHistory,
    currentUnit: AppUnit,
    shape: RoundedCornerShape,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(36.dp)
            .background(color = MaterialTheme.colorScheme.surfaceVariant, shape = shape)
            .border(width = 1.5.dp, color = MaterialTheme.colorScheme.primary, shape = shape)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left Side: Amount and Debug ID placed horizontally next to each other
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp) // Gap between amount and ID
        ) {
            // 1. MAIN AMOUNT
            Text(
                text = stringResource(
                    currentUnit.formatRes,
                    currentUnit.convertFromMl(item.amountMl)
                ),
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                ),
                maxLines = 1
            )

            // 2. DEBUG ID (Placed right next to the amount, easy to strip out later)
            Text(
                text = "[ID: ${item.id}]",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Light
                ),
                maxLines = 1
            )
        }

        // Right Side: 3. TIMESTAMP
        Text(
            text = item.timeLogged,
            style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            ),
            maxLines = 1
        )
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun TodayHistoryDarkModePreview(
    @PreviewParameter(HistoryLogsProvider ::class) mockHistory: List<DrunkCupHistory>
) {
    ErtawyTheme {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            TodayHistorySection(
                historyItems = mockHistory,
                currentUnit = AppUnit.ML,
                onDeleteLog = {}
            )
        }
    }
}