package android.waterreminder.ui.dashboard.components

import android.content.res.Configuration
import android.waterreminder.ui.theme.ErtawyTheme
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun CustomWaterInputDialog(
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var textInput by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Custom Intake",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            OutlinedTextField(
                value = textInput,
                onValueChange = { newValue ->
                    // Only allow numeric input
                    if (newValue.all { it.isDigit() }) {
                        textInput = newValue
                        val amount = newValue.toIntOrNull() ?: 0
                        // Highlight error state if the amount exceeds a normal daily single intake limit
                        isError = amount > 3000
                    }
                },
                label = { Text("Amount (ml)") },
                placeholder = { Text("e.g. 400") },
                isError = isError,
                supportingText = {
                    if (isError) {
                        Text("Please enter a valid amount below 3000 ml")
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                enabled = textInput.isNotEmpty() && !isError,
                onClick = {
                    val intakeAmount = textInput.toIntOrNull()
                    if ((intakeAmount != null) && (intakeAmount > 0)) {
                        onConfirm(intakeAmount)
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Preview(name = "Light Mode", group = "Themes", showBackground = true)
@Preview(name = "Dark Mode", group = "Themes", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun CustomWaterInputDialogPreview() {
    ErtawyTheme {
        // Surface provides the default systemic background fill color tokens for the dialog backdrop
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            CustomWaterInputDialog(
                onDismiss = {},
                onConfirm = {}
            )
        }
    }
}