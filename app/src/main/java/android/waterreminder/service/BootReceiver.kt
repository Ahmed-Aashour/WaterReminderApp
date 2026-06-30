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
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Intercepts low-level system startup signals to re-arm tracking alarms after a device reboot.
 *
 * Because Android's [android.app.AlarmManager] completely clears all scheduled alarms upon a device
 * power-down or restart, this receiver acts as a self-healing node to restore consistency to the
 * recurring background notification cycle.
 *
 * Requires `android.permission.RECEIVE_BOOT_COMPLETED` declaration inside the `AndroidManifest.xml`.
 */
@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject lateinit var appSettingsDataStore: AppSettingsDataStore
    @Inject lateinit var resolveTrackingWindowUseCase: ResolveTrackingWindowUseCase

    /**
     * Responds to device startup broadcast signals.
     *
     * Validates incoming intent strings against platform boot actions, claims an asynchronous execution
     * token via [goAsync], and evaluates user preference states to schedule the next applicable reminder.
     *
     * @param context The execution environment context supplied by the OS.
     * @param intent The broadcast configuration carrying system intent actions.
     */
    override fun onReceive(context: Context, intent: Intent) {
        // Guard Clause: Only proceed if action matches official system startup signals
        if (intent.action != Intent.ACTION_BOOT_COMPLETED && intent.action != "android.intent.action.QUICKBOOT_POWERON") {
            return
        }

        Log.d(TAG, "Device reboot detected! Rescheduling hydration tracking engine...")

        // Request async token to signal the OS to keep our process alive
        val pendingResult = goAsync()

        // Scope confined inside the active execution path to prevent memory leaks
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        val appContext = context.applicationContext
        val scheduler = HydrationReminderScheduler(appContext)

        scope.launch {
            try {
                val prefs = appSettingsDataStore.settingsFlow.first()

                // Guard Clause: If notifications are turned off globally, abort early
                if (!prefs.areNotificationsEnabled) {
                    Log.d(TAG, "Reminders are disabled. Skipping initialization.")
                    return@launch
                }

                // Resolve operational time frames
                val window = resolveTrackingWindowUseCase.execute(prefs)

                scheduler.scheduleNextReminder(
                    startTime = window.first,
                    endTime = window.second,
                    intervalMinutes = prefs.frequency
                )
                Log.d(TAG, "Hydration reminders successfully restored on boot.")

            } catch (e: Exception) {
                Log.e(TAG, "Critical failure while recovering reminders on boot: ${e.message}", e)
            } finally {
                // Complete the async process block and release the OS lease
                pendingResult.finish()
                scope.cancel()
            }
        }
    }

    companion object {
        private const val TAG = "BootReceiver"
    }
}