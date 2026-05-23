package android.waterreminder.ui

import android.content.res.Configuration
import android.waterreminder.R
import android.waterreminder.data.WaterDataStore
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

// Brand Color
val HydraBlue = Color(0xFF2EA9EB)
// Custom Typography Font (font asset is in res/font/dg_ghayaty_regular.ttf)
val DGGhayaty = FontFamily(
    Font(R.font.dg_ghayaty_regular, weight = FontWeight.Normal),
)
// re-usable base style mapping the Figma italic layout
val italicStyle = TextStyle(
    fontFamily = DGGhayaty,
    fontStyle = FontStyle.Italic,
    fontWeight = FontWeight.W400,
)

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
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // HEADER
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    colors = CardDefaults.cardColors(Color.White),
                    shape = RoundedCornerShape(5.dp),
                    modifier = Modifier.size(width = 214.dp, height = 60.dp),
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = "Hydra-ate\nYourself",
                            style = italicStyle,
                            fontSize = 28.sp,
                            color = HydraBlue,
                        )
                    }
                }

                // Reset icon mapped neatly adjacent to the custom header banner
                IconButton(onClick = onResetWater) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset Progress",
                        tint = HydraBlue
                    )
                }
            }

            // RADIAL PROGRESS BAR
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(5.dp),
                modifier = Modifier.size(width = 245.dp, height = 249.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator(
                        progress = { progressFraction },
                        modifier = Modifier.size(200.dp),
                        color = HydraBlue,
                        trackColor = HydraBlue.copy(alpha = 0.3f),
                        strokeWidth = 10.dp,
                        strokeCap = StrokeCap.Round
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "$progressPercentage%",
                            style = italicStyle,
                            fontSize = 32.sp,
                            color = HydraBlue,
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "$currentIntake / $dailyTarget ml",
                            style = italicStyle,
                            fontSize = 12.sp,
                            color = HydraBlue.copy(alpha = 0.9f),
                        )
                    }
                }
            }

            // HISTORY CARD (Cups Empty State)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (progressPercentage != 100) "Press a cup below to add" else "Fully Hydrated! Great Job! 😎",
                    style = italicStyle,
                    fontSize = 16.sp,
                    color = HydraBlue,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Card(
                    colors = CardDefaults.cardColors(HydraBlue),
                    shape = RoundedCornerShape(5.dp),
                    modifier = Modifier.fillMaxWidth().height(94.dp),
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (currentIntake == 0) "No Cups Drank!" else "Hydra Log Active!",
                            style = italicStyle,
                            fontSize = 40.sp,
                            color = Color.White,
                        )
                    }
                }
            }

            // MULTI-OPTION QUICK DRINK DOCK
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Generic factory item macro function for modularity
                val dynamicAmounts = listOf(250, 350, 500)
                dynamicAmounts.forEach { amount ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Button(
                            onClick = { onIncrementWater(amount) },
                            colors = ButtonDefaults.buttonColors(containerColor = HydraBlue),
                            shape = RoundedCornerShape(5.dp),
                            modifier = Modifier.size(width = 53.dp, height = 78.dp),
                            contentPadding = PaddingValues(0.dp),
                        ) {
                            Text("🥛", fontSize = 24.dp.value.sp)
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "${amount}ml",
                            style = italicStyle,
                            fontSize = 16.sp,
                            color = HydraBlue,
                        )
                    }
                }

                // Plus Input Button Custom Amount
                IconButton(
                    onClick = { onIncrementWater(100) },
                    modifier = Modifier
                        .padding(bottom = 20.dp)
                        .size(34.dp)
                        .background(HydraBlue, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Custom Add",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

/**
 * A preview displaying what the UI looks like initially
 */
@Preview(
    showBackground = true,
    name = "Initial State"
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
 * A preview displaying what the UI looks like when the goal is "partially complete"
 */
@Preview(
    showBackground = true,
    name = "Partial State",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun WaterDashboardPreviewThreeForthFull() {
    MaterialTheme {
        Surface {
            WaterDashboardContent(
                currentIntake = 1250+625,
                dailyTarget = 2500,
                progressFraction = 0.75f,
                progressPercentage = 75,
                onIncrementWater = {},
                onResetWater = {}
            )
        }
    }
}

/**
 * A preview displaying what the UI looks like when the goal is "fully complete"
 */
@Preview(
    showBackground = true,
    name = "Full State",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun WaterDashboardPreviewFull() {
    MaterialTheme {
        Surface {
            WaterDashboardContent(
                currentIntake = 2500,
                dailyTarget = 2500,
                progressFraction = 1f,
                progressPercentage = 100,
                onIncrementWater = {},
                onResetWater = {}
            )
        }
    }
}