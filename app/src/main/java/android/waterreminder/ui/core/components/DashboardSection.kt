package android.waterreminder.ui.core.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * A reusable dashboard structural container component that enforces consistent section
 * header layouts and spacing guidelines.
 *
 * @param title The plain text key displaying the section purpose.
 * @param modifier Explicit outer modifiers to control structural parent constraints.
 * @param content The slot lambda to append any target body element layout directly beneath the header.
 */
@Composable
fun DashboardSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier.width(342.dp) // Locks your structural footprint across components
    ) {
        // Uniform Section Title Block with clipping fixes included
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        // Inject whatever component layout is passed into the slot right here
        content()
    }
}