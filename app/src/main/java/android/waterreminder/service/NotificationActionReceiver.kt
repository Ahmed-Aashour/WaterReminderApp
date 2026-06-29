package android.waterreminder.service

import android.app.NotificationManager
import android.app.RemoteInput
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.waterreminder.data.repository.WaterRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotificationActionReceiver : BroadcastReceiver() {

    @Inject lateinit var waterRepository: WaterRepository

    // Separate background scope to perform asynchronous repository writes safely.
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = context.getSystemService(NotificationManager::class.java)

        val amountMl = when (intent.action) {
            ACTION_QUICK_DRINK -> {
                Log.d(TAG, "Quick drink triggered via banner.")
                DEFAULT_QUICK_DRINK_AMOUNT_ML
            }
            ACTION_CUSTOM_DRINK -> {
                Log.d(TAG, "Custom drink input received.")
                intent.getCustomDrinkAmount()
            }
            else -> null
        }

        if (amountMl == null || amountMl <= 0) {
            notificationManager.cancel(NOTIFICATION_ID)
            return
        }

        val pendingResult = goAsync()
        scope.launch {
            try {
                waterRepository.logWaterConsumption(amountMl)
                notificationManager.cancel(NOTIFICATION_ID)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun Intent.getCustomDrinkAmount(): Int? {
        return RemoteInput.getResultsFromIntent(this)
            ?.getCharSequence(KEY_CUSTOM_WATER_AMOUNT)
            ?.toString()
            ?.toIntOrNull()
    }

    companion object {
        private const val TAG = "NotificationAction"
        private const val NOTIFICATION_ID = 1
        private const val DEFAULT_QUICK_DRINK_AMOUNT_ML = 250

        const val ACTION_QUICK_DRINK = "ACTION_QUICK_DRINK"
        const val ACTION_CUSTOM_DRINK = "ACTION_CUSTOM_DRINK"
        const val KEY_CUSTOM_WATER_AMOUNT = "KEY_CUSTOM_WATER_AMOUNT"
    }
}
