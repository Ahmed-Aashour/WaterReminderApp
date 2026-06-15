package android.waterreminder.ui.settings.components

import android.content.res.Configuration
import android.waterreminder.ui.core.components.BaseDialog
import android.waterreminder.ui.core.components.CancelButton
import android.waterreminder.ui.core.components.SelectionRow
import android.waterreminder.ui.settings.SettingsUiState
import android.waterreminder.ui.settings.preview.SettingsScreenStateProvider
import android.waterreminder.ui.theme.ErtawyTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter

@Composable
fun UnitDialog(
    currentUnit: String,
    supportedUnits: List<String>,
    onUnitSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    BaseDialog(
        title = "Unit",
        onDismissRequest = onDismiss,
        modifier = modifier,
        buttons = {
            CancelButton(onClick = onDismiss)
        },
        options = {
            supportedUnits.forEach { unitLabel ->
                SelectionRow(
                    label = unitLabel,
                    isSelected = currentUnit == unitLabel,
                    onClick = {
                        onUnitSelected(unitLabel)
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
            currentUnit = "ml",
            supportedUnits = state.supportedUnits,
            onUnitSelected = {},
            onDismiss = {}
        )
    }
}