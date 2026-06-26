package android.waterreminder.service.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AladhanApiService {

    // API endpoint specification targeting the dynamic timings by city route
    @GET("timingsByCity/{date}")
    suspend fun getTimingsByCity(
        @Path("date") dateString: String,
        @Query("city") city: String,
        @Query("country") country: String,
        @Query("method") calculationMethod: Int
    ): AladhanApiResponse
}

data class AladhanApiResponse(
    val data: PrayerData
)

data class PrayerData(
    val timings: PrayerTimings
)

data class PrayerTimings(
    val fajr: String,
    val maghrib: String
)