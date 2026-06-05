package android.waterreminder.data.dao

import android.waterreminder.data.entity.CupsCatalogEntity
import android.waterreminder.data.entity.WaterHistoryEntity
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DashboardDao {

    // --- History Logs Actions ---

    @Insert
    suspend fun insertLog(log: WaterHistoryEntity)

    @Delete
    suspend fun deleteLog(log: WaterHistoryEntity)

    // Using Flow means your UI will instantly update when a user adds or deletes a cup
    @Query("SELECT * FROM water_history ORDER BY timestamp DESC")
    fun getAllHistoryFlow(): Flow<List<WaterHistoryEntity>>

    // Query to grab only today's data (ViewModel uses this to build today_history_logs)
    @Query("SELECT * FROM water_history WHERE timestamp >= :startOfDayTimestamp ORDER BY timestamp DESC")
    fun getTodayHistoryFlow(startOfDayTimestamp: Long): Flow<List<WaterHistoryEntity>>


    // --- Cups Catalog Actions ---

    @Insert(onConflict = OnConflictStrategy.IGNORE) // Ignores duplicates if user tries to add an existing size
    suspend fun insertCup(cup: CupsCatalogEntity)

    @Delete
    suspend fun deleteCup(cup: CupsCatalogEntity)

    @Query("SELECT * FROM cups_catalog ORDER BY amountMl ASC")
    fun getCupsCatalogFlow(): Flow<List<CupsCatalogEntity>>
}