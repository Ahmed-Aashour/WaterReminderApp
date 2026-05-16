package android.waterreminder

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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