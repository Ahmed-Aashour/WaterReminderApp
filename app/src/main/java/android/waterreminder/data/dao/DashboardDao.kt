package android.waterreminder.data.dao

import android.waterreminder.data.entity.CupsCatalogEntity
import android.waterreminder.data.entity.WaterHistoryEntity
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface DashboardDao {

    // --- History Logs Actions ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: WaterHistoryEntity)

    @Delete
    suspend fun deleteLog(log: WaterHistoryEntity)

    @Query("DELETE FROM water_history WHERE id = :logId")
    suspend fun deleteLogById(logId: Long)

    @Query("SELECT * FROM water_history ORDER BY timestamp DESC")
    fun getAllHistoryFlow(): Flow<List<WaterHistoryEntity>>

    @Query("SELECT * FROM water_history WHERE timestamp >= :startOfDayTimestamp ORDER BY timestamp DESC")
    fun getTodayHistoryFlow(startOfDayTimestamp: Long): Flow<List<WaterHistoryEntity>>

    /**
     * 🚀 High Optimization: Let SQLite calculate the progress sum instantly.
     * Your UI can observe this directly instead of mapping and calculating inside the ViewModel.
     * Coalesce returns 0 instead of null if no cups have been drank today yet.
     */
    @Query("SELECT COALESCE(SUM(amountMl), 0) FROM water_history WHERE timestamp >= :startOfDayTimestamp")
    fun getTodayTotalIntakeFlow(startOfDayTimestamp: Long): Flow<Int>


    // --- Cups Catalog Actions ---

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCup(cup: CupsCatalogEntity)

    @Delete
    suspend fun deleteCup(cup: CupsCatalogEntity)

    @Query("SELECT * FROM cups_catalog ORDER BY amountMl ASC")
    fun getCupsCatalogFlow(): Flow<List<CupsCatalogEntity>>


    // --- Daily Streak Actions ---

    @Query("SELECT * FROM water_history WHERE timestamp >= :sinceTimestamp ORDER BY timestamp ASC")
    fun getHistorySinceFlow(sinceTimestamp: Long): Flow<List<WaterHistoryEntity>>

    /**
     * Clear all history logs cleanly within a thread-safe database transaction block.
     */
    @Transaction
    @Query("DELETE FROM water_history")
    suspend fun clearAllHistory()
}