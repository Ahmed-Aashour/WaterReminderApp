package android.waterreminder.ui.core.components

import android.waterreminder.ui.theme.ErtawyTypography
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTimeInput(
    label: String,
    state: TimePickerState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            style = ErtawyTypography.sectionStyle,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.Start)
        )

        MaterialTheme(
            typography = MaterialTheme.typography.copy(
                // The hour/minute input digits & colon
                displayMedium = ErtawyTypography.titleStyle,
                // AM/PM selector labels
                labelLarge = ErtawyTypography.normalStyle,
                // The inner helper text fields ("Hour" / "Minute")
                bodySmall = ErtawyTypography.smallStyle,
            )
        ) {
            TimeInput(
                state = state,
                modifier = Modifier.padding(vertical = 4.dp),
                colors = TimePickerDefaults.colors(
                    // Time input square background & numbers
                    timeSelectorSelectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    timeSelectorSelectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    timeSelectorUnselectedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    timeSelectorUnselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,

                    // AM/PM switch container & text colors
                    periodSelectorSelectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    periodSelectorSelectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    periodSelectorUnselectedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    periodSelectorUnselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    periodSelectorBorderColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}