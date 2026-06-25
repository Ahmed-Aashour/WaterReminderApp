package android.waterreminder.ui.settings.components

import android.content.res.Configuration
import android.waterreminder.R
import android.waterreminder.ui.core.components.BaseDialog
import android.waterreminder.ui.core.components.CancelButton
import android.waterreminder.ui.core.components.SelectionRow
import android.waterreminder.ui.settings.SettingsUiState
import android.waterreminder.ui.settings.preview.SettingsScreenStateProvider
import android.waterreminder.ui.theme.ErtawyTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter

@Composable
fun FrequencyDialog(
    currentFrequency: Int,
    frequencyOptions: List<Int>,
    onFrequencySelected: (Int) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    BaseDialog(
        title = stringResource(R.string.frequency_dialog_title),
        onDismissRequest = onDismiss,
        modifier = modifier,
        buttons = {
            CancelButton(onClick = onDismiss)
        },
        options = {
            frequencyOptions.forEach { minutes ->
                SelectionRow(
                    label = pluralStringResource(
                        id = R.plurals.frequency_minutes_format,
                        count = minutes,
                        minutes
                    ),
                    isSelected = currentFrequency == minutes,
                    onClick = {
                        onFrequencySelected(minutes)
                        onDismiss() // Fluid auto-dismiss upon selecting a row choice
                    }
                )
            }
        }
    )
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun FrequencyDialogPreview(
    @PreviewParameter(SettingsScreenStateProvider::class) state: SettingsUiState
) {
    ErtawyTheme {
        FrequencyDialog(
            currentFrequency = state.frequency,
            frequencyOptions = state.supportedFrequencies,
            onFrequencySelected = {},
            onDismiss = {}
        )
    }
}