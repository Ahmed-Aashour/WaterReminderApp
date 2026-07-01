package android.waterreminder.ui.dashboard.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import android.waterreminder.R
import android.waterreminder.ui.core.components.BaseDialog
import android.waterreminder.ui.core.components.CancelButton
import android.waterreminder.ui.core.components.ConfirmButton

@Composable
fun ClearHistoryConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    BaseDialog(
        title = stringResource(R.string.clear_history_dialog_title),
        onDismissRequest = onDismiss,
        modifier = modifier,
        options = {
            Text(
                text = stringResource(R.string.clear_history_dialog_description),
                style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        buttons = {
            CancelButton(onClick = onDismiss)
            ConfirmButton(onClick = onConfirm)
        }
    )
}