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

import android.os.Bundle
import android.waterreminder.service.WaterNotificationScheduler
import android.waterreminder.ui.navigation.WaterTrackerNavHost
import android.waterreminder.ui.theme.ErtawyTheme
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val notificationScheduler by lazy { WaterNotificationScheduler(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Instantly unlocks transparent status/navigation bars
        enableEdgeToEdge()
        // Let the scheduler handle the setup implicitly
        notificationScheduler.scheduleRepeatingReminders()

        setContent {
            ErtawyTheme {
                WaterTrackerNavHost(
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}