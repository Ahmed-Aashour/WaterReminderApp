package android.waterreminder.ui.core.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * A reusable dashboard structural container component that enforces consistent section
 * header layouts and spacing guidelines.
 *
 * @param title The plain text key displaying the section purpose.
 * @param modifier Explicit outer modifiers to control structural parent constraints.
 * @param actionButton An optional action component placed in-line with the section title.
 * @param content The slot lambda to append any target body element layout directly beneath the header.
 */
@Composable
fun DashboardSection(
    title: String,
    modifier: Modifier = Modifier,
    actionButton: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier.width(342.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f, fill = false)
            )

            if (actionButton != null) {
                actionButton()
            }
        }

        content()
    }
}