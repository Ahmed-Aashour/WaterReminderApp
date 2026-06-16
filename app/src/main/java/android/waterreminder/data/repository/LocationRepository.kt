package android.waterreminder.data.repository

import android.waterreminder.data.entity.DeviceLocationEntity

interface LocationRepository {
    suspend fun getCurrentLocationProfile(): DeviceLocationEntity?
}