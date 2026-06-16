package android.waterreminder.service.init

import android.waterreminder.data.store.AppSettingsDataStore
import kotlinx.coroutines.flow.first
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppInitializer @Inject constructor(
    private val appSettingsDataStore: AppSettingsDataStore
) {
    /**
     * Inspects the current device configuration profile and seamlessly updates
     * data persistence layer elements to match local geographic regions out of the box.
     */
    suspend fun initializeDefaultUserRegion() {
        val currentPrefs = appSettingsDataStore.settingsFlow.first()

        // Safeguard: Never overwrite anything if data already exists!
        if (currentPrefs.city.isBlank() && currentPrefs.country.isBlank()) {
            val systemLocale = Locale.getDefault()

            // Extracts the full readable name of the country (e.g., "Egypt", "Germany")
            val localizedCountry = systemLocale.displayCountry

            if (!localizedCountry.isNullOrBlank()) {
                appSettingsDataStore.updateLocationProfile(
                    city = "", // Geocoder coordinates will resolve this later
                    country = localizedCountry
                )
            }
        }
    }
}