package android.waterreminder.ui.core.components

import android.content.res.Configuration
import android.waterreminder.R
import android.waterreminder.ui.theme.ErtawyTheme
import android.waterreminder.ui.theme.ErtawyTypography
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CancelButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String = stringResource(R.string.action_cancel)
) {
    TextButton(
        onClick = onClick,
        modifier = modifier.height(40.dp)
    ) {
        Text(
            text = text,
            style = ErtawyTypography.sectionStyle,
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
    Button(
        onClick = onClick,
        modifier = modifier.height(40.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        shape = MaterialTheme.shapes.medium
    ) {
        Text(
            text = text,
            style = ErtawyTypography.sectionStyle
        )
    }
}

@Composable
fun ClearButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String = stringResource(R.string.action_clear) // Ensure action_clear is defined in strings.xml (e.g., "Clear")
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(40.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
        shape = MaterialTheme.shapes.medium
    ) {
        Text(
            text = text,
            style = ErtawyTypography.sectionStyle
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
            }
        }
    }
}