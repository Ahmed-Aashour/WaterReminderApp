package android.waterreminder.service.init

import android.waterreminder.data.store.AppSettingsDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Orchestrates pre-dashboard application initialization routines.
 *
 * This singleton utility handles localized device environment inspection to provision standard
 * fallback profile preferences seamlessly before the onboarding layout renders.
 *
 * @property appSettingsDataStore The reactive data persistence wrapper handling user profile configurations.
 */
@Singleton
class AppInitializer @Inject constructor(
    private val appSettingsDataStore: AppSettingsDataStore
) {
    /**
     * Inspects the active device configuration profile and provisions regional fallback properties.
     *
     * To prevent destructively overwriting pre-existing configurations, this function uses a
     * strict safety check: it will only write properties if both the saved city and country records
     * are blank. It reads the system's global [Locale] configuration to extract an explicit
     * 2-letter ISO Alpha-2 country token (e.g., "US", "DE", "EG") to ensure consistent regional mapping.
     *
     * Swifts execution entirely over to [Dispatchers.Default] to protect calling threads from locale lookup blocks.
     */
    suspend fun initializeDefaultUserRegion() = withContext(Dispatchers.Default) {
        val currentPrefs = appSettingsDataStore.settingsFlow.first()

        // Safeguard: Never overwrite anything if user data already exists!
        if (currentPrefs.city.isBlank() && currentPrefs.country.isBlank()) {
            val systemLocale = Locale.getDefault()

            // Extracts the stable ISO 2-letter code ("EG", "US")
            val countryIsoCode = systemLocale.country

            if (!countryIsoCode.isNotBlank()) {
                appSettingsDataStore.updateLocationProfile(
                    city = "", // Geocoder coordinates will resolve this later
                    country = countryIsoCode
                )
            }
        }
    }
}