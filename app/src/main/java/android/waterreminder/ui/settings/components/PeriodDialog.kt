package android.waterreminder.ui.settings.components

import android.content.res.Configuration
import android.waterreminder.ui.core.components.BaseDialog
import android.waterreminder.ui.core.components.CancelButton
import android.waterreminder.ui.core.components.ConfirmButton
import android.waterreminder.ui.core.components.CustomTimePicker
import android.waterreminder.ui.settings.SettingsUiState
import android.waterreminder.ui.settings.preview.SettingsScreenStateProvider
import android.waterreminder.ui.theme.ErtawyTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeriodDialog(
    currentStartTime: String,
    currentEndTime: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.US)

    // Independent State Engine for Start Time
    val parsedStartTime = runCatching { LocalTime.parse(currentStartTime, timeFormatter) }.getOrNull()
    val startTimeState = rememberTimePickerState(
        initialHour = parsedStartTime?.hour ?: 7,
        initialMinute = parsedStartTime?.minute ?: 0,
        is24Hour = false
    )

    // Independent State Engine for End Time
    val parsedEndTime = runCatching { LocalTime.parse(currentEndTime, timeFormatter) }.getOrNull()
    val endTimeState = rememberTimePickerState(
        initialHour = parsedEndTime?.hour ?: 21,
        initialMinute = parsedEndTime?.minute ?: 0,
        is24Hour = false
    )

    BaseDialog(
        title = "Reminder Period",
        onDismissRequest = onDismiss,
        modifier = modifier.width(340.dp), // Expanded slightly to provide breathing room for inputs
        options = {
            // Use a scrollable container so smaller device screens handle dual clock heights easily
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(420.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(28.dp)
            ) {
                // Embedded Start Picker instance
                CustomTimePicker(
                    label = "Active Start Time",
                    state = startTimeState
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))

                // Embedded End Picker instance
                CustomTimePicker(
                    label = "Active End Time",
                    state = endTimeState
                )
            }
        },
        buttons = {
            CancelButton(onClick = onDismiss)
            ConfirmButton(
                onClick = {
                    // Extract data straight from local states
                    val finalStartTime = LocalTime.of(
                        startTimeState.hour,
                        startTimeState.minute
                    ).format(timeFormatter)
                    val finalEndTime = LocalTime.of(
                        endTimeState.hour,
                        endTimeState.minute
                    ).format(timeFormatter)

                    onConfirm(finalStartTime, finalEndTime)
                    onDismiss()
                }
            )
        }
    )
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