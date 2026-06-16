package android.waterreminder.ui.settings.components

import android.content.res.Configuration
import android.waterreminder.ui.core.components.BaseDialog
import android.waterreminder.ui.core.components.CancelButton
import android.waterreminder.ui.core.components.ConfirmButton
import android.waterreminder.ui.core.components.SelectionRow
import android.waterreminder.ui.settings.SettingsUiState
import android.waterreminder.ui.settings.preview.SettingsScreenStateProvider
import android.waterreminder.ui.theme.ErtawyTheme
import android.waterreminder.ui.theme.ErtawyTypography
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

private enum class TimePickingTarget {
    START, END
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeriodDialog(
    currentStartTime: String,
    currentEndTime: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Hold local user alterations before hitting "Confirm"
    var selectedStartTime by remember { mutableStateOf(currentStartTime) }
    var selectedEndTime by remember { mutableStateOf(currentEndTime) }

    // Sub-dialog picker controls
    var activePickingTarget by remember { mutableStateOf<TimePickingTarget?>(null) }
    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.US)

    BaseDialog(
        title = "Start-End Times",
        onDismissRequest = onDismiss,
        modifier = modifier,
        options = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Tap a metric to edit details:",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Start Time Selection Trigger
                SelectionRow(
                    label = "Start Time: $selectedStartTime",
                    isSelected = activePickingTarget == TimePickingTarget.START,
                    onClick = { activePickingTarget = TimePickingTarget.START }
                )

                // End Time Selection Trigger
                SelectionRow(
                    label = "End Time: $selectedEndTime",
                    isSelected = activePickingTarget == TimePickingTarget.END,
                    onClick = { activePickingTarget = TimePickingTarget.END }
                )
            }
        },
        buttons = {
            CancelButton(onClick = onDismiss)
            ConfirmButton(
                onClick = {
                    onConfirm(selectedStartTime, selectedEndTime)
                    onDismiss()
                }
            )
        }
    )

    // Inner standard Material 3 Clock face popup
    if (activePickingTarget != null) {
        val calendar = Calendar.getInstance()
        val initialTimeStr = if (activePickingTarget == TimePickingTarget.START) selectedStartTime else selectedEndTime

        // Safely pre-populate the clock hands to match current selections or fallback to system time
        val parsedTime = runCatching { LocalTime.parse(initialTimeStr, timeFormatter) }.getOrNull()
        val initialHour = parsedTime?.hour ?: calendar.get(Calendar.HOUR_OF_DAY)
        val initialMinute = parsedTime?.minute ?: calendar.get(Calendar.MINUTE)

        val timePickerState = rememberTimePickerState(
            initialHour = initialHour,
            initialMinute = initialMinute,
            is24Hour = false
        )

        AlertDialog(
            onDismissRequest = { activePickingTarget = null },
            confirmButton = {
                TextButton(
                    onClick = {
                        val formattedTime = LocalTime.of(timePickerState.hour, timePickerState.minute).format(timeFormatter)
                        if (activePickingTarget == TimePickingTarget.START) {
                            selectedStartTime = formattedTime
                        } else {
                            selectedEndTime = formattedTime
                        }
                        activePickingTarget = null
                    }
                ) {
                    Text("OK", style = ErtawyTypography.sectionStyle)
                }
            },
            dismissButton = {
                TextButton(onClick = { activePickingTarget = null }) {
                    Text("Cancel", style = ErtawyTypography.sectionStyle)
                }
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (activePickingTarget == TimePickingTarget.START) "Select Start Time" else "Select End Time",
                        style = ErtawyTypography.titleStyle,
                        modifier = Modifier
                            .padding(bottom = 24.dp)
                            .align(Alignment.Start)
                    )
                    TimePicker(state = timePickerState)
                }
            }
        )
    }
}

@Preview(name = "Light Mode", group = "Themes", showBackground = true)
@Preview(name = "Dark Mode", group = "Themes", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PeriodDialogPreview(
    @PreviewParameter(SettingsScreenStateProvider::class) state: SettingsUiState
) {
    ErtawyTheme {
        PeriodDialog(
            currentStartTime = state.startTime,
            currentEndTime = state.endTime,
            onDismiss = {},
            onConfirm = { _, _ -> },
        )
    }
}