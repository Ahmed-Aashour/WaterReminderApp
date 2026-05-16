package android.waterreminder.ui

import android.content.res.Configuration
import android.waterreminder.data.WaterDataStore
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
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
            verticalArrangement = Arrangement.SpaceBetween // Spreads out ring vs buttons beautifully
        ) {

            // Header spacing layout block
            Spacer(modifier = Modifier.height(16.dp))

            // 1. THE HYDRATION RING CONTAINER
            // Box allows us to layer the text directly over the center of the circular indicator
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(260.dp) // Large explicit footprint for the ring dashboard
            ) {
                // Background Track Ring (Shows the uncompleted goal space cleanly)
                CircularProgressIndicator(
                    progress = { 1.0f },
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    strokeWidth = 14.dp,
                    strokeCap = StrokeCap.Round
                )

                // Active Progress Ring Layer
                CircularProgressIndicator(
                    progress = { progressFraction },
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 14.dp,
                    strokeCap = StrokeCap.Round // Smoothly rounds the edges of the progress line
                )

                // Core Stats Stacked inside the Ring center
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "$progressPercentage%",
                        fontSize = 54.sp,
                        style = MaterialTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "$currentIntake / $dailyTarget ml",
                        fontSize = 16.sp,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Message label
            Text(
                text = if (progressFraction >= 1.0f) "Goal Achieved! 🎉" else "Stay Hydrated!",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            // 2. ACTION CONTROLS FOOTER
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { onIncrementWater(250) },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .padding(horizontal = 8.dp)
                ) {
                    Text("+250 ml", fontSize = 16.sp)
                }

                Button(
                    onClick = { onIncrementWater(500) },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .padding(horizontal = 8.dp)
                ) {
                    Text("+500 ml", fontSize = 16.sp)
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