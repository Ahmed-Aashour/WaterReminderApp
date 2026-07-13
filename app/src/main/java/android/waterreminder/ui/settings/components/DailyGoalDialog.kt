package android.waterreminder.ui.settings.components

import android.content.res.Configuration
import android.waterreminder.R
import android.waterreminder.data.entity.AppUnit
import android.waterreminder.ui.core.components.*
import android.waterreminder.ui.settings.SettingsUiState
import android.waterreminder.ui.settings.preview.SettingsScreenStateProvider
import android.waterreminder.ui.theme.ErtawyTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyGoalDialog(
    currentGoalMl: Int,
    currentUnit: AppUnit,
    predefinedOptions: List<Int>,
    validationEvents: Flow<String>,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit,
    onConfirmCustomString: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Local processing states
    var selectedGoalMl by remember { mutableStateOf(currentGoalMl.takeIf { ml -> predefinedOptions.any { it == ml } }) }
    var customInputString by remember { mutableStateOf(if (selectedGoalMl == null) currentGoalMl.toString() else "") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Listen to ViewModel hot channel validation alerts
    LaunchedEffect(validationEvents) {
        validationEvents.collectLatest { error ->
            errorMessage = error
        }
    }

    BaseDialog(
        title = stringResource(R.string.daily_goal_dialog_title),
        onDismissRequest = onDismiss,
        modifier = modifier,
        options = {
            // Render pre-defined selections loop safely
            predefinedOptions.forEach { amountMl ->
                SelectionRow(
                    label = stringResource(currentUnit.formatRes, currentUnit.convertFromMl(amountMl)),
                    isSelected = selectedGoalMl == amountMl,
                    onClick = {
                        selectedGoalMl = amountMl
                        customInputString = ""
                        errorMessage = null
                    }
                )
            }
        },
        inputField = {
            TextInputField(
                value = customInputString,
                onValueChange = { input ->
                    customInputString = input
                    selectedGoalMl = null
                    errorMessage = null
                },
                isError = errorMessage != null
            )

            // Error message readout trace
            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                )
            }
        },
        buttons = {
            CancelButton(onClick = onDismiss)
            ConfirmButton(
                onClick = {
                    errorMessage = null
                    if (selectedGoalMl != null) {
                        onConfirm(selectedGoalMl!!)
                        onDismiss()
                    } else {
                        onConfirmCustomString(customInputString)
                    }
                }
            )
        }
    )
}

@Preview(name = "Light Mode", group = "Themes", showBackground = true)
@Preview(name = "Dark Mode", group = "Themes", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DailyGoalDialogPreview(
    @PreviewParameter(SettingsScreenStateProvider::class) state: SettingsUiState
) {
    ErtawyTheme {
        DailyGoalDialog(
            currentGoalMl = state.dailyGoalMl,
            currentUnit = state.unit,
            predefinedOptions = state.predefinedGoals,
            validationEvents = MutableSharedFlow(),
            onDismiss = {},
            onConfirm = {},
            onConfirmCustomString = {}
        )
    }
}