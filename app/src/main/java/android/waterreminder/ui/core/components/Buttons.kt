package android.waterreminder.ui.core.components

import android.waterreminder.ui.theme.ErtawyTypography
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CancelButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String = "Cancel"
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
    text: String = "Confirm"
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