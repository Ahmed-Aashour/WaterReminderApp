package android.waterreminder.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.waterreminder.data.entity.DeviceLocationEntity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    private val locationClient: FusedLocationProviderClient,
    @param:ApplicationContext private val context: Context
) : LocationRepository {

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocationProfile(): DeviceLocationEntity? {
        // Fallback directly if Geocoder is missing or platform is broken
        if (!Geocoder.isPresent()) return null

        return try {
            // Fetch the last known location or current high accuracy location snapshot
            val location = locationClient.getCurrentLocation(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                null
            ).await() ?: return null

            val geocoder = Geocoder(context, Locale.US)

            // Reverse geocode the coordinate pair to extract human-readable location labels
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            val matchingAddress = addresses?.firstOrNull()

            if (matchingAddress != null) {
                DeviceLocationEntity(
                    city = matchingAddress.locality ?: matchingAddress.subAdminArea ?: "Alexandria",
                    country = matchingAddress.countryName ?: "Egypt"
                )
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null // Return null on network or system exceptions to trigger upstream fallback paths
        }
    }
}