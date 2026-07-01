package android.waterreminder.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.waterreminder.data.entity.DeviceLocationEntity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.resume

/**
 * Production implementation of the [LocationRepository] infrastructure contract.
 *
 * Interceptors high-level GPS coordinates via Google Play Services [FusedLocationProviderClient]
 * and reverse-geocodes raw latitude/longitude points into user-readable city and country models.
 */
class LocationRepositoryImpl @Inject constructor(
    private val locationClient: FusedLocationProviderClient,
    @param:ApplicationContext private val context: Context
) : LocationRepository {

    /**
     * Retrieves the current device location profile and translates it into an address entity.
     *
     * Enforces explicit safety by wrapping execution context inside `Dispatchers.IO` to protect
     * older devices from synchronous network-thread blocking during legacy Geocoder executions.
     *
     * @return A valid [DeviceLocationEntity] containing localized metadata, or null if permissions
     * are missing, GPS hardware is unresponsive, or reverse-geocoding lookup times out.
     */
    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocationProfile(): DeviceLocationEntity? = withContext(Dispatchers.IO) {
        if (!Geocoder.isPresent()) return@withContext null

        try {
            val location = locationClient.getCurrentLocation(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                null
            ).await() ?: return@withContext null

            val geocoder = Geocoder(context, Locale.US)

            // Dynamic API Routing to navigate background engine deprecations safely
            val addresses = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                suspendCancellableCoroutine { continuation ->
                    geocoder.getFromLocation(
                        location.latitude,
                        location.longitude,
                        1,
                        object : Geocoder.GeocodeListener {
                            override fun onGeocode(addresses: MutableList<Address>) {
                                // Safely verify coroutine lifecycle before resuming
                                if (continuation.isActive) {
                                    continuation.resume(addresses)
                                }
                            }
                            override fun onError(errorMessage: String?) {
                                if (continuation.isActive) {
                                    continuation.resume(null)
                                }
                            }
                        }
                    )
                    // Ensure that if the coroutine scope is torn down, we don't hold dangling listener references
                    continuation.invokeOnCancellation { /* No-op: framework GC handles interface drop */ }
                }
            } else {
                // Safe legacy fallback running natively on worker threads inside our IO wrapper context
                @Suppress("DEPRECATION")
                geocoder.getFromLocation(location.latitude, location.longitude, 1)
            }

            val matchingAddress = addresses?.firstOrNull()

            if (matchingAddress != null) {
                DeviceLocationEntity(
                    city = matchingAddress.locality ?: matchingAddress.subAdminArea ?: "",
                    country = matchingAddress.countryName ?: ""
                )
            } else null
        } catch (e: Exception) {
            // TODO: In a production app, pass this exception along to your crash reporting framework (e.g. Firebase Crashlytics)
            e.printStackTrace()
            null
        }
    }
}