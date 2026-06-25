package android.waterreminder.ui.settings.components

import android.content.res.Configuration
import android.waterreminder.R
import android.waterreminder.data.entity.AppTheme
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
fun ThemeDialog(
    currentTheme: AppTheme,
    onThemeSelected: (AppTheme) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    BaseDialog(
        title = stringResource(R.string.theme_dialog_title),
        onDismissRequest = onDismiss,
        modifier = modifier,
        buttons = {
            CancelButton(onClick = onDismiss)
        },
        options = {
            AppTheme.entries.forEach { themeOption ->
                SelectionRow(
                    label = stringResource(id = themeOption.getDisplayLabelRes()),
                    isSelected = currentTheme == themeOption,
                    onClick = {
                        onThemeSelected(themeOption)
                        onDismiss() // Fluid auto-dismiss matching app requirements
                    }
                )
            }
        }
    )
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ThemeDialogPreview(
    @PreviewParameter(SettingsScreenStateProvider::class) state: SettingsUiState
) {
    ErtawyTheme {
        ThemeDialog(
            currentTheme = state.theme,
            onThemeSelected = {},
            onDismiss = {}
        )
    }
}