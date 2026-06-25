package android.waterreminder.ui.settings.components

import android.content.res.Configuration
import android.waterreminder.R
import android.waterreminder.data.entity.AppLanguage
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
fun LanguageDialog(
    currentLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    BaseDialog(
        title = stringResource(R.string.language_dialog_title),
        onDismissRequest = onDismiss,
        modifier = modifier,
        buttons = {
            CancelButton(onClick = onDismiss)
        },
        options = {
            AppLanguage.entries.forEach { languageOption ->
                SelectionRow(
                    label = stringResource(id = languageOption.getDisplayLabelRes()),
                    isSelected = currentLanguage == languageOption,
                    onClick = {
                        onLanguageSelected(languageOption)
                        onDismiss() // Fluid auto-dismiss matching app design requirements
                    }
                )
            }
        }
    )
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun LanguageDialogPreview(
    @PreviewParameter(SettingsScreenStateProvider::class) state: SettingsUiState
) {
    ErtawyTheme {
        LanguageDialog(
            currentLanguage = state.language,
            onLanguageSelected = {},
            onDismiss = {}
        )
    }
}