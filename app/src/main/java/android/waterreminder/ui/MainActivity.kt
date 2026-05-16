/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.waterreminder.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.waterreminder.WaterDataStore
import android.waterreminder.WaterReminderReceiver
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Instantiate the data storage layer
    private val waterDataStore by lazy { WaterDataStore(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Schedule a test reminder alarm when the app opens
        scheduleTestReminder(this)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WaterDashboard(waterDataStore)
                }
            }
        }
    }

    private fun scheduleTestReminder(context: Context) {
        val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager

        // Checking if the app is legally allowed to schedule exact alarms
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Log.w("WaterReminder", "Cannot schedule exact alarm: Permission denied by system/user.")
                // Fallback: Use an inexact alarm which doesn't require special permission
                scheduleInexactReminder(context, alarmManager)
                return
            }
        }

        // Alarm setup
        val intent = Intent(context, WaterReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerTime = System.currentTimeMillis() + 10000

        // Wrap the scheduling call inside a try-catch block as a safety net
        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        } catch (e: SecurityException) {
            Log.e("WaterReminder", "SecurityException caught while scheduling alarm", e)
            scheduleInexactReminder(context, alarmManager)
        }
    }

    // Fallback method to prevent the engine from breaking entirely
    private fun scheduleInexactReminder(context: Context, alarmManager: AlarmManager) {
        val intent = Intent(context, WaterReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerTime = System.currentTimeMillis() + 10000

        // setAndAllowWhileIdle lets Android shift the timing slightly to save battery, bypasses the restriction
        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            pendingIntent
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterDashboard(dataStore: WaterDataStore) {
    // Collect asynchronous flow states from DataStore dynamically into Compose State
    val currentIntake by dataStore.waterIntakeFlow.collectAsState(initial = 0)
    val dailyTarget by dataStore.waterTargetFlow.collectAsState(initial = 2000)

    // Coroutine scope is required to call the suspend write operations of DataStore
    val scope = rememberCoroutineScope()

    // Calculate the progress metrics
    // Perform decimal division, then clamp it between 0.0f and 1.0f for the progress bar.
    val progressFraction = if (dailyTarget > 0) {
        (currentIntake.toFloat() / dailyTarget.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }
    // Calculate percentage integer (e.g., 0.45 becomes 45)
    val progressPercentage = (progressFraction * 100).toInt()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Water Tracker") },
                actions = {
                    IconButton(onClick = {
                        scope.launch { dataStore.resetWater() }
                    }) {
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
            // 2. Visual Progress Indicator
            // LinearProgressIndicator expects a value between 0.0 (0%) and 1.0 (100%)
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

            // 3. Display Percentage Metric
            Text(
                text = "$progressPercentage%",
                fontSize = 64.sp,
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Main Metric Volume Display
            Text(
                text = "$currentIntake / $dailyTarget ml",
                fontSize = 20.sp,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(64.dp))

            // Action Quick Log Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { scope.launch { dataStore.incrementWater(250) } },
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                ) {
                    Text("+250 ml")
                }

                Button(
                    onClick = { scope.launch { dataStore.incrementWater(500) } },
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                ) {
                    Text("+500 ml")
                }
            }
        }
    }
}