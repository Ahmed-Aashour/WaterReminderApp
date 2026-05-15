package android.waterreminder

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// 1. Create a single instance of DataStore using the property delegate
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "water_tracker_prefs")

class WaterDataStore(private val context: Context) {

    companion object {
        // 2. Define the keys for the values we want to save
        val WATER_INTAKE_KEY = intPreferencesKey("current_water_intake")
        val WATER_TARGET_KEY = intPreferencesKey("daily_water_target")
    }

    // 3. Read Data: Expose the water intake as a Flow stream
    val waterIntakeFlow: Flow<Int> = context.dataStore.data
        .map { preferences ->
            // Default value is 0 if nothing has been saved yet
            preferences[WATER_INTAKE_KEY] ?: 0
        }

    // Expose the daily target as a Flow stream (Default: 2000ml / 2 Liters)
    val waterTargetFlow: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[WATER_TARGET_KEY] ?: 2000
        }

    // 4. Write Data: Increment water intake safely via a suspend function (Default: 250ml)
    suspend fun incrementWater(amount: Int = 250) {
        context.dataStore.edit { preferences ->
            val currentIntake = preferences[WATER_INTAKE_KEY] ?: 0
            preferences[WATER_INTAKE_KEY] = currentIntake + amount
        }
    }

    // Reset water intake (for a new day)
    suspend fun resetWater() {
        context.dataStore.edit { preferences ->
            preferences[WATER_INTAKE_KEY] = 0
        }
    }

    // Update the daily goal target
    suspend fun updateTarget(newTarget: Int) {
        context.dataStore.edit { preferences ->
            preferences[WATER_TARGET_KEY] = newTarget
        }
    }
}