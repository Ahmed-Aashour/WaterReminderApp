package android.waterreminder.ui.core.components

import android.waterreminder.ui.theme.ErtawyTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun Header(
    title: String,
    modifier: Modifier = Modifier,
    actionButton: @Composable () -> Unit // Decoupled injection slot for action items
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(45.dp), // Figma height configuration
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge,
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
        Box(modifier = Modifier.padding(16.dp)) {
            Header(
                title = "Ertawy",
                actionButton = {
                    SquareIconButton(
                        icon = Icons.Default.Settings,
                        contentDescription = "Settings",
                        onClick = {}
                    )
                }
            )
        }
    }
}