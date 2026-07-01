package android.waterreminder.service.remote

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit contract mapping communication pathways with the Aladhan prayer times engine.
 *
 * This service is utilized to dynamically calculate local solar positioning matrices,
 * which the application requires to automatically determine active hydration-tracking windows
 * for users participating in intermittent fasting or religious fasting windows.
 */
interface AladhanApiService {

    /**
     * Resolves localized prayer time tracking metrics for a designated calendar date.
     *
     * Example payload endpoint path matching target: "v1/timingsByCity/01-07-2026"
     *
     * @param dateString The chronological target formatted explicitly as "dd-MM-yyyy".
     * @param city The geographical municipality query parameter (e.g., "Cairo").
     * @param country The geographic nation state or ISO Alpha-2 country code string container (e.g., "EG").
     * @param calculationMethod The method coordinate code defining calculation parameters (e.g., 5 for Egyptian General Authority).
     * @return A strongly-typed [AladhanApiResponse] mirroring the server response data tree.
     */
    @GET("timingsByCity/{date}")
    suspend fun getTimingsByCity(
        @Path("date") dateString: String,
        @Query("city") city: String,
        @Query("country") country: String,
        @Query("method") calculationMethod: Int
    ): AladhanApiResponse
}

/**
 * High-level wrapper containing root network payloads delivered back from the Aladhan server engine.
 */
data class AladhanApiResponse(
    @SerializedName("data")
    val data: PrayerData
)

/**
 * Intermediate data packet structural branch containing nested timing entities.
 */
data class PrayerData(
    @SerializedName("timings")
    val timings: PrayerTimings
)

/**
 * Concrete immutable entity container holding raw string timing representations of specific solar events.
 *
 * Time stamps are delivered uniformly as 24-hour military clock string records (e.g., "04:12" or "19:43").
 */
data class PrayerTimings(
    @SerializedName("Fajr")
    val fajr: String,

    @SerializedName("Maghrib")
    val maghrib: String
)