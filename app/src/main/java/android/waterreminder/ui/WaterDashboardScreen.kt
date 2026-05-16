package android.waterreminder.ui

import android.content.res.Configuration
import android.waterreminder.data.WaterDataStore
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

/**
 * 1. STATEFUL CONTAINER
 * This manages the data collection pipeline and handles asynchronous events.
 */
@Composable
fun WaterDashboardScreen(dataStore: WaterDataStore) {
    val currentIntake by dataStore.waterIntakeFlow.collectAsState(initial = 0)
    val dailyTarget by dataStore.waterTargetFlow.collectAsState(initial = 2000)
    val scope = rememberCoroutineScope()

    // Perform the progress math safely
    val progressFraction = if (dailyTarget > 0) {
        (currentIntake.toFloat() / dailyTarget.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }
    val progressPercentage = (progressFraction * 100).toInt()

    // Pass pure, state-free data down into the Stateless layout component
    WaterDashboardContent(
        currentIntake = currentIntake,
        dailyTarget = dailyTarget,
        progressFraction = progressFraction,
        progressPercentage = progressPercentage,
        onIncrementWater = { amount ->
            scope.launch { dataStore.incrementWater(amount) }
        },
        onResetWater = {
            scope.launch { dataStore.resetWater() }
        }
    )
}

/**
 * 2. STATELESS UI LAYOUT
 * This only handles visual rendering. It is decoupled from DataStore completely.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterDashboardContent(
    currentIntake: Int,
    dailyTarget: Int,
    progressFraction: Float,
    progressPercentage: Int,
    onIncrementWater: (Int) -> Unit,
    onResetWater: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Water Tracker") },
                actions = {
                    IconButton(onClick = onResetWater) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset Progress")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LinearProgressIndicator(
                progress = { progressFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "$progressPercentage%",
                fontSize = 64.sp,
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "$currentIntake / $dailyTarget ml",
                fontSize = 20.sp,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(64.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { onIncrementWater(250) },
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                ) {
                    Text("+250 ml")
                }

                Button(
                    onClick = { onIncrementWater(500) },
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                ) {
                    Text("+500 ml")
                }
            }
        }
    }
}

/**
 * A standard, crisp Light Mode preview using mock data
 */
@Preview(
    showBackground = true,
    name = "Light Mode - Initial State"
)
@Composable
fun WaterDashboardPreviewEmpty() {
    MaterialTheme {
        Surface {
            WaterDashboardContent(
                currentIntake = 0,
                dailyTarget = 2000,
                progressFraction = 0.0f,
                progressPercentage = 0,
                onIncrementWater = {}, // Empty lambda: clicks do nothing in static preview
                onResetWater = {}
            )
        }
    }
}

/**
 * A preview displaying what the UI looks like when the goal is partially complete,
 * forced into Android's Dark Mode system configuration.
 */
@Preview(
    showBackground = true,
    name = "Dark Mode - Progress State",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun WaterDashboardPreviewHalfFull() {
    MaterialTheme {
        Surface {
            WaterDashboardContent(
                currentIntake = 1250,
                dailyTarget = 2500,
                progressFraction = 0.5f,
                progressPercentage = 50,
                onIncrementWater = {},
                onResetWater = {}
            )
        }
    }
}