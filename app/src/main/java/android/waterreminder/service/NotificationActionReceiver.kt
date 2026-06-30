package android.waterreminder.service

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.waterreminder.data.repository.WaterRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotificationActionReceiver : BroadcastReceiver() {

    @Inject lateinit var waterRepository: WaterRepository

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        Log.d(TAG, "Received notification broadcast action: $action")

        // Guard Clause: Only handle intent actions explicitly owned by this receiver contract
        if (action != ACTION_QUICK_DRINK) {
            Log.w(TAG, "Unrecognized action received. Aborting execution.")
            return
        }

        // Extract the explicit dynamic amount sent via the notification intent bundle
        val amountMl = intent.getIntExtra(EXTRA_WATER_AMOUNT, -1)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Safety Guard: Validate that the extracted amount is structurally realistic
        if (amountMl <= 0) {
            Log.e(TAG, "Invalid or missing water amount extra ($amountMl Ml). Dismissing notification.")
            notificationManager.cancel(NOTIFICATION_ID)
            return
        }

        // Acquire background process lease from OS before launching async repository operations
        val pendingResult = goAsync()
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        scope.launch {
            try {
                Log.d(TAG, "Logging $amountMl ml of water consumption from push button input...")
                waterRepository.logWaterConsumption(amountMl)

                // Cancel/dismiss the banner seamlessly upon successful database save
                notificationManager.cancel(NOTIFICATION_ID)
                Log.d(TAG, "Water log complete. Active hydration notification cleared.")

            } catch (e: Exception) {
                Log.e(TAG, "Failed to write hydration entry from background action receiver: ${e.message}", e)
            } finally {
                // 🚀 Clean up scopes and release process priorities back to Android OS
                pendingResult.finish()
                scope.cancel()
            }
        }
    }

    companion object {
        private const val TAG = "NotificationActionReceiver"

        // Centralized identity token for dismissals and canvas modifications
        const val NOTIFICATION_ID = 1001

        // 🚀 Fully qualified namespace contracts preventing global OS collisions
        const val ACTION_QUICK_DRINK = "android.waterreminder.action.QUICK_DRINK"
        const val EXTRA_WATER_AMOUNT = "android.waterreminder.extra.WATER_AMOUNT"
    }
}