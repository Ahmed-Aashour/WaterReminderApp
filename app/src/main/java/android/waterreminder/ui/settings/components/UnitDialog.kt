package android.waterreminder.ui.settings.components

import android.content.res.Configuration
import android.waterreminder.R
import android.waterreminder.data.entity.AppUnit
import android.waterreminder.ui.core.components.BaseDialog
import android.waterreminder.ui.core.components.CancelButton
import android.waterreminder.ui.core.components.SelectionRow
import android.waterreminder.ui.settings.SettingsUiState
import android.waterreminder.ui.settings.preview.SettingsScreenStateProvider
import android.waterreminder.ui.theme.ErtawyTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter

@Composable
fun UnitDialog(
    currentUnit: AppUnit,
    onUnitSelected: (AppUnit) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    BaseDialog(
        title = stringResource(R.string.unit_dialog_title),
        onDismissRequest = onDismiss,
        modifier = modifier,
        buttons = {
            CancelButton(onClick = onDismiss)
        },
        options = {
            AppUnit.entries.forEach { unitOption ->
                SelectionRow(
                    label = stringResource(id = unitOption.getDisplayLabelRes()),
                    isSelected = currentUnit == unitOption,
                    onClick = {
                        onUnitSelected(unitOption)
                        onDismiss() // Auto-dismiss upon selection change for a crisp UX
                    }
                )
            }
        }
    )
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun UnitDialogPreview(
    @PreviewParameter(SettingsScreenStateProvider::class) state: SettingsUiState
) {
    ErtawyTheme {
        UnitDialog(
            currentUnit = state.unit,
            onUnitSelected = {},
            onDismiss = {}
        )
    }
}