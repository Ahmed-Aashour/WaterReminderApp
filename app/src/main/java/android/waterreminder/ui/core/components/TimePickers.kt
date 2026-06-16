package android.waterreminder.ui.core.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import android.waterreminder.ui.theme.ErtawyTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTimePicker(
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

        // Render the Material 3 clock face directly inline
        TimePicker(
            state = state
        )
    }
}