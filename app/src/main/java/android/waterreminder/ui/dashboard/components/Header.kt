package android.waterreminder.ui.dashboard.components

import android.waterreminder.ui.core.components.SettingsButton
import android.waterreminder.ui.theme.ErtawyTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import android.waterreminder.ui.theme.ErtawyTypography
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun Header(
    title: String,
    modifier: Modifier = Modifier,
    actionButton: @Composable () -> Unit // Decoupled injection slot for action items
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(42.dp), // Figma height configuration
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = ErtawyTypography.titleStyle,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.wrapContentHeight(Alignment.CenterVertically)
        )

        // Render the injected button component inside the structural layout grid
        actionButton()
    }
}

@Preview(name = "Dashboard Header Block", showBackground = true)
@Composable
fun HeaderPreview() {
    ErtawyTheme {
        Header(
            title = "Ertawy",
            actionButton = {
                SettingsButton(onClick = {})
            }
        )
    }
}