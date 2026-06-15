package android.waterreminder.ui.settings.components

import android.content.res.Configuration
import android.waterreminder.ui.core.components.BaseDialog
import android.waterreminder.ui.core.components.SelectionRow
import android.waterreminder.ui.settings.GoalOptionUiModel
import android.waterreminder.ui.settings.SettingsUiState
import android.waterreminder.ui.settings.preview.SettingsScreenStateProvider
import android.waterreminder.ui.theme.ErtawyTheme
import android.waterreminder.ui.theme.ErtawyTypography
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyGoalDialog(
    predefinedOptions: List<GoalOptionUiModel>,
    currentGoalMl: Int,
    currentUnit: String,
    validationEvents: SharedFlow<String>,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit,
    onConfirmCustomString: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Local processing states
    var selectedGoalMl by remember { mutableStateOf(currentGoalMl.takeIf { ml -> predefinedOptions.any { it.amountMl == ml } }) }
    var customInputString by remember { mutableStateOf(if (selectedGoalMl == null) currentGoalMl.toString() else "") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Listen to ViewModel hot channel validation alerts
    LaunchedEffect(validationEvents) {
        validationEvents.collectLatest { error ->
            errorMessage = error
        }
    }

    BaseDialog(
        title = "Goal",
        onDismissRequest = onDismiss,
        modifier = modifier,
        buttons = {
            // Cancel Button
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.height(40.dp)
            ) {
                Text(text = "Cancel", style = ErtawyTypography.sectionStyle, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.width(10.dp))
            // Confirm Button
            Button(
                onClick = {
                    errorMessage = null
                    if (selectedGoalMl != null) {
                        onConfirm(selectedGoalMl!!)
                        onDismiss()
                    } else {
                        onConfirmCustomString(customInputString)
                        // Note: If an error is fired from ViewModel, validationEvents channel clears it
                        // and sets errorMessage above, preventing dismiss.
                    }
                },
                modifier = Modifier.height(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(text = "Confirm", style = ErtawyTypography.sectionStyle)
            }
        }
    ) {
        // Render pre-defined selections loop safely
        predefinedOptions.forEach { option ->
            val displayLabel = if (currentUnit == "ml") option.displayLabelMl else option.displayLabelOz
            SelectionRow(
                label = displayLabel,
                isSelected = selectedGoalMl == option.amountMl,
                onClick = {
                    selectedGoalMl = option.amountMl
                    customInputString = ""
                    errorMessage = null
                }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Custom Input Container Layer
        OutlinedTextField(
            value = customInputString,
            onValueChange = { input ->
                customInputString = input
                selectedGoalMl = null // Break predefined active selection
                errorMessage = null   // Clear prior alert tracks instantly on modify
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp), // Extra headroom to handle dynamic subtext alerts cleanly
            textStyle = ErtawyTypography.sectionStyle,
            placeholder = { Text("Custom...", style = ErtawyTypography.sectionStyle, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)) },
            singleLine = true,
            isError = errorMessage != null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = TextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.primary,
                unfocusedTextColor = MaterialTheme.colorScheme.primary,
                focusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                unfocusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                errorContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
                errorIndicatorColor = MaterialTheme.colorScheme.error
            )
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
    }
}

@Preview(name = "Light Mode", group = "Themes", showBackground = true)
@Preview(name = "Dark Mode", group = "Themes", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DailyGoalDialogPreview(
    @PreviewParameter(SettingsScreenStateProvider::class) state: SettingsUiState
) {
    ErtawyTheme {
        DailyGoalDialog(
            predefinedOptions = state.predefinedGoalOptions,
            currentGoalMl = state.dailyGoalMl,
            currentUnit = state.measurementUnit,
            validationEvents = MutableSharedFlow(),
            onDismiss = {},
            onConfirm = {},
            onConfirmCustomString = {}
        )
    }
}