package android.waterreminder.ui.core.components

import android.content.res.Configuration
import android.waterreminder.R
import android.waterreminder.ui.theme.ErtawyTheme
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CancelButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String = stringResource(R.string.action_cancel)
) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun ConfirmButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String = stringResource(R.string.action_confirm)
) {
    BaseActionButton(
        text = text,
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        borderColor = MaterialTheme.colorScheme.primary,
        modifier = modifier
    )
}

@Composable
fun ClearButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String = stringResource(R.string.action_clear)
) {
    BaseActionButton(
        text = text,
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
        borderColor = MaterialTheme.colorScheme.error,
        modifier = modifier
    )
}

@Composable
fun EditButton(
    isEditing: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    editText: String = stringResource(R.string.action_edit),
    doneText: String = stringResource(R.string.action_done)
) {
    BaseActionButton(
        text = if (isEditing) doneText else editText,
        onClick = onClick,
        containerColor = if (isEditing) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        contentColor = if (isEditing) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.primary,
        borderColor = MaterialTheme.colorScheme.primary,
        modifier = modifier
    )
}

@Composable
private fun BaseActionButton(
    text: String,
    onClick: () -> Unit,
    containerColor: Color,
    contentColor: Color,
    borderColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(containerColor)
            .border(BorderStroke(1.dp, borderColor), CircleShape)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = contentColor
        )
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DialogButtonsPreview() {
    ErtawyTheme {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CancelButton(onClick = {})
                ConfirmButton(onClick = {})
                ClearButton(onClick = {})
                EditButton(isEditing = false, onClick = {})
                EditButton(isEditing = true, onClick = {})
            }
        }
    }
}