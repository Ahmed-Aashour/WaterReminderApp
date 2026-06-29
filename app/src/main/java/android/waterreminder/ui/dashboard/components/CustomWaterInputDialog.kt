package android.waterreminder.ui.dashboard.components

import android.content.res.Configuration
import android.waterreminder.R
import android.waterreminder.data.entity.AppUnit
import android.waterreminder.ui.core.components.BaseDialog
import android.waterreminder.ui.core.components.CancelButton
import android.waterreminder.ui.core.components.ConfirmButton
import android.waterreminder.ui.core.components.TextInputField
import android.waterreminder.ui.theme.ErtawyTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CustomWaterInputDialog(
    currentUnit: AppUnit,
    validationEvents: Flow<String>,
    onDismiss: () -> Unit,
    onConfirmCustomString: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var customInputString by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val unitLabel = stringResource(currentUnit.unitRes)

    // Listen to ViewModel hot channel validation alerts
    LaunchedEffect(validationEvents) {
        validationEvents.collectLatest { error ->
            errorMessage = error
        }
    }

    BaseDialog(
        title = stringResource(R.string.dashboard_custom_intake_dialog_title),
        onDismissRequest = onDismiss,
        modifier = modifier,
        options = {},
        inputField = {
            TextInputField(
                value = customInputString,
                onValueChange = { input ->
                    if (input.all { it.isDigit() }) {
                        customInputString = input
                        errorMessage = null
                    }
                },
                placeholderText = stringResource(
                    R.string.dashboard_custom_intake_placeholder_format,
                    unitLabel
                ),
                isError = errorMessage != null
            )

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
                    onConfirmCustomString(customInputString)
                }
            )
        }
    )
}

@Preview(name = "Light Mode", group = "Themes", showBackground = true)
@Preview(name = "Dark Mode", group = "Themes", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun CustomWaterInputDialogPreview() {
    ErtawyTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            CustomWaterInputDialog(
                currentUnit = AppUnit.ML,
                validationEvents = MutableSharedFlow(),
                onDismiss = {},
                onConfirmCustomString = {},
            )
        }
    }
}
