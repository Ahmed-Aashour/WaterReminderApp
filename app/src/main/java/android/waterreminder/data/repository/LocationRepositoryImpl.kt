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
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.resume

class LocationRepositoryImpl @Inject constructor(
    private val locationClient: FusedLocationProviderClient,
    @param:ApplicationContext private val context: Context
) : LocationRepository {

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocationProfile(): DeviceLocationEntity? {
        if (!Geocoder.isPresent()) return null

        return try {
            val location = locationClient.getCurrentLocation(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                null
            ).await() ?: return null

            val geocoder = Geocoder(context, Locale.US)

            // 🌟 Dynamic API Routing to handle the deprecation cleanly
            val addresses = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Android 13+ (API 33+): Use the modern non-blocking callback API wrapped in a coroutine bridge
                suspendCancellableCoroutine { continuation ->
                    geocoder.getFromLocation(location.latitude, location.longitude, 1, object : Geocoder.GeocodeListener {
                        override fun onGeocode(addresses: MutableList<Address>) {
                            continuation.resume(addresses)
                        }
                        override fun onError(errorMessage: String?) {
                            continuation.resume(null)
                        }
                    })
                }
            } else {
                // Older Android versions: Safe to suppress and fallback to legacy synchronous lookup
                @Suppress("DEPRECATION")
                geocoder.getFromLocation(location.latitude, location.longitude, 1)
            }

            val matchingAddress = addresses?.firstOrNull()

            if (matchingAddress != null) {
                DeviceLocationEntity(
                    city = matchingAddress.locality ?: matchingAddress.subAdminArea ?: "Alexandria",
                    country = matchingAddress.countryName ?: "Egypt"
                )
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}