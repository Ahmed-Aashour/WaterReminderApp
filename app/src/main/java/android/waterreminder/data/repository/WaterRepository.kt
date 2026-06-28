package android.waterreminder.data.repository

import android.waterreminder.data.dao.DashboardDao
import android.waterreminder.data.entity.CupsCatalogEntity
import android.waterreminder.data.entity.WaterHistoryEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WaterRepository @Inject constructor(
    private val dashboardDao: DashboardDao
) {

    // --- Cups Catalog Streams & Actions ---

    /**
     * Streams the entire custom cup catalog list.
     */
    fun getCupsCatalog(): Flow<List<CupsCatalogEntity>> =
        dashboardDao.getCupsCatalogFlow()

    /**
     * Safely saves a new custom cup size into the database configuration.
     */
    suspend fun addCupToCatalog(amountMl: Int) {
        dashboardDao.insertCup(CupsCatalogEntity(amountMl = amountMl))
    }

    /**
     * Removes a custom cup size from the catalog configuration.
     */
    suspend fun removeCupFromCatalog(amountMl: Int) {
        dashboardDao.deleteCup(CupsCatalogEntity(amountMl = amountMl))
    }


    // --- History Logs Streams & Actions ---

    /**
     * Emits a reactive list containing only the data points logged during the current calendar day.
     * 💡 Note: Pass startOfDay dynamically from the ViewModel to ensure midnight boundaries shift correctly.
     */
    fun getTodayHistoryLogs(startOfTodayTimestamp: Long): Flow<List<WaterHistoryEntity>> {
        return dashboardDao.getTodayHistoryFlow(startOfTodayTimestamp)
    }

    /**
     * 🚀 High Optimization: Directly exposes the SQLite calculated daily total sum stream.
     * Eliminates object allocations and manual list maps inside your presentation layers.
     */
    fun getTodayTotalIntake(startOfTodayTimestamp: Long): Flow<Int> {
        return dashboardDao.getTodayTotalIntakeFlow(startOfTodayTimestamp)
    }

    /**
     * Logs a new water consumption entry into the persistent database.
     */
    suspend fun logWaterConsumption(amountMl: Int) {
        val newLog = WaterHistoryEntity(
            amountMl = amountMl,
            timestamp = System.currentTimeMillis()
        )
        dashboardDao.insertLog(newLog)
    }

    /**
     * Deletes a water consumption entry from the history table using its explicit identifier.
     */
    suspend fun deleteWaterLog(logId: Long) {
        dashboardDao.deleteLogById(logId)
    }

    /**
     * Streams all history logs recorded since a specific timestamp milestone.
     */
    fun getHistorySince(timestamp: Long): Flow<List<WaterHistoryEntity>> {
        return dashboardDao.getHistorySinceFlow(timestamp)
    }

    /**
     * Completely wipes out all history data metrics within a secure database transaction.
     */
    suspend fun clearAllHistoryLogs() {
        dashboardDao.clearAllHistory()
    }
}