package android.waterreminder.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.waterreminder.data.store.AppSettingsDataStore
import android.waterreminder.service.usecase.ResolveTrackingWindowUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject lateinit var appSettingsDataStore: AppSettingsDataStore
    @Inject lateinit var resolveTrackingWindowUseCase: ResolveTrackingWindowUseCase

    // Use an IO-bound SupervisorJob scope to survive brief asynchronous network/disk tasks
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        // Only proceed if the incoming action matches the official device startup signals
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == "android.intent.action.QUICKBOOT_POWERON") {
            Log.d("BootReceiver", "Device reboot detected! Rescheduling hydration tracking engine...")

            val appContext = context.applicationContext
            val scheduler = WaterNotificationScheduler(appContext)

            scope.launch {
                try {
                    val prefs = appSettingsDataStore.settingsFlow.first()

                    // Guard Clause: If notifications are turned off globally, do absolutely nothing
                    if (!prefs.areNotificationsEnabled) {
                        Log.d("BootReceiver", "Reminders are disabled. Skipping adjustment.")
                        return@launch
                    }

                    // Resolve operational time frames (including dynamic fasting constraints if active)
                    val window = resolveTrackingWindowUseCase.execute(prefs)

                    scheduler.scheduleNextReminder(
                        startTime = window.startTime,
                        endTime = window.endTime,
                        intervalMinutes = prefs.frequency
                    )
                    Log.d("BootReceiver", "Hydration reminders successfully restored on boot.")

                } catch (e: Exception) {
                    Log.e("BootReceiver", "Critical failure while recovering reminders on boot: ${e.message}", e)
                }
            }
        }
    }
}