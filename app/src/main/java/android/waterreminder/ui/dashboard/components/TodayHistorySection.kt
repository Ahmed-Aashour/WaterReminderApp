package android.waterreminder.ui.dashboard.components

import android.content.res.Configuration
import android.waterreminder.R
import android.waterreminder.data.entity.AppUnit
import android.waterreminder.ui.core.components.ClearButton
import android.waterreminder.ui.core.components.DashboardSection
import android.waterreminder.ui.core.components.EditButton
import android.waterreminder.ui.dashboard.DrunkCupHistory
import android.waterreminder.ui.dashboard.preview.HistoryLogsProvider
import android.waterreminder.ui.theme.ErtawyTheme
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
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
    onClearAllHistoryTrigger: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isEditMode by remember { mutableStateOf(false) }

    LaunchedEffect(historyItems.isEmpty()) {
        if (historyItems.isEmpty()) {
            isEditMode = false
        }
    }

    DashboardSection(
        title = stringResource(R.string.dashboard_section_history_title),
        actionButton = {
            if (historyItems.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    EditButton(
                        isEditing = isEditMode,
                        onClick = { isEditMode = !isEditMode }
                    )

                    if (isEditMode) {
                        ClearButton(onClick = onClearAllHistoryTrigger)
                    }
                }
            }
        },
        modifier = modifier
    ) {
        if (historyItems.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(16.dp))
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top
            ) {
                historyItems.forEachIndexed { index, item ->
                    val rowShape = when {
                        historyItems.size == 1 -> RoundedCornerShape(12.dp)
                        index == 0 -> RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                        index == historyItems.lastIndex -> RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
                        else -> RectangleShape
                    }

                    HistoryRow(
                        item = item,
                        currentUnit = currentUnit,
                        shape = rowShape,
                        isEditMode = isEditMode,
                        onDeleteClick = { onDeleteLog(item) }
                    )

                    if (index < historyItems.lastIndex) {
                        HorizontalDivider(thickness = 1.5.dp, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryRow(
    item: DrunkCupHistory,
    currentUnit: AppUnit,
    shape: Shape,
    isEditMode: Boolean,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(42.dp)
            .background(color = MaterialTheme.colorScheme.surfaceVariant, shape = shape)
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
                text = stringResource(currentUnit.formatRes, currentUnit.convertFromMl(item.amountMl)),
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

        // Right Side: 3. TIMESTAMP & Delete-Icon in Edit Mode
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            Text(
                text = item.timeLogged,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                ),
                maxLines = 1
            )

            AnimatedVisibility(
                visible = isEditMode,
                enter = fadeIn() + scaleIn(initialScale = 0.7f),
                exit = fadeOut() + scaleOut(targetScale = 0.7f)
            ) {
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.dashboard_accessibility_delete_log),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
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
                onDeleteLog = {},
                onClearAllHistoryTrigger = {},
            )
        }
    }
}