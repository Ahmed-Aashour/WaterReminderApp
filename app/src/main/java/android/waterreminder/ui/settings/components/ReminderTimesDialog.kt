package android.waterreminder.ui.settings.components

import android.content.res.Configuration
import android.waterreminder.R
import android.waterreminder.ui.core.components.BaseDialog
import android.waterreminder.ui.core.components.CancelButton
import android.waterreminder.ui.core.components.ConfirmButton
import android.waterreminder.ui.core.components.CustomTimeInput
import android.waterreminder.ui.settings.SettingsUiState
import android.waterreminder.ui.settings.preview.SettingsScreenStateProvider
import android.waterreminder.ui.theme.ErtawyTheme
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderTimesDialog(
    currentStartTime: LocalTime,
    currentEndTime: LocalTime,
    onDismiss: () -> Unit,
    onConfirm: (LocalTime, LocalTime) -> Unit,
    modifier: Modifier = Modifier
) {
    // Independent State Engine for Start Time
    val startTimeState = rememberTimePickerState(
        initialHour = currentStartTime.hour,
        initialMinute = currentStartTime.minute,
        is24Hour = false
    )

    // Independent State Engine for End Time
    val endTimeState = rememberTimePickerState(
        initialHour = currentEndTime.hour,
        initialMinute = currentEndTime.minute,
        is24Hour = false
    )

    BaseDialog(
        title = stringResource(R.string.reminder_times_dialog_title),
        onDismissRequest = onDismiss,
        modifier = modifier.width(340.dp), // Expanded slightly to provide breathing room for inputs
        options = {
            CustomTimeInput(
                label = stringResource(R.string.label_active_start_time),
                state = startTimeState
            )
            CustomTimeInput(
                label = stringResource(R.string.label_active_end_time),
                state = endTimeState
            )
        },
        buttons = {
            CancelButton(onClick = onDismiss)
            ConfirmButton(
                onClick = {
                    val finalStartTime = LocalTime.of(startTimeState.hour, startTimeState.minute)
                    val finalEndTime = LocalTime.of(endTimeState.hour, endTimeState.minute)
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
fun ReminderTimesDialogPreview(
    @PreviewParameter(SettingsScreenStateProvider::class) state: SettingsUiState
) {
    ErtawyTheme {
        ReminderTimesDialog(
            currentStartTime = state.startTime,
            currentEndTime = state.endTime,
            onDismiss = {},
            onConfirm = { _, _ -> },
        )
    }
}