package android.waterreminder.service

import android.app.NotificationManager
import android.app.RemoteInput
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.waterreminder.data.WaterDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class NotificationActionReceiver : BroadcastReceiver() {

    // Separate background scope to perform asynchronous DataStore writes safely
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        val dataStore = WaterDataStore(context.applicationContext)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        when (intent.action) {
            "ACTION_QUICK_DRINK" -> {
                Log.d("NotificationAction", "Quick drink triggered via banner.")
                scope.launch {
                    dataStore.incrementWater(250) // Default quick intake amount
                    notificationManager.cancel(1) // Dismiss the notification banner
                }
            }
            "ACTION_CUSTOM_DRINK" -> {
                Log.d("NotificationAction", "Custom drink input received.")
                // Extract the typed text bundle using the system key
                val remoteInput = RemoteInput.getResultsFromIntent(intent)
                val customAmountText = remoteInput?.getCharSequence("KEY_CUSTOM_WATER_AMOUNT")?.toString()

                val customAmount = customAmountText?.toIntOrNull() ?: 0

                if (customAmount > 0) {
                    scope.launch {
                        dataStore.incrementWater(customAmount)
                        notificationManager.cancel(1) // Dismiss the notification banner
                    }
                } else {
                    // If parsing failed (e.g. user typed letters), just dismiss cleanly
                    notificationManager.cancel(1)
                }
            }
        }
    }
}