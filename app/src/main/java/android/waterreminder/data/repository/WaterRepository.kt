package android.waterreminder.data.repository

import android.waterreminder.data.dao.DashboardDao
import android.waterreminder.data.entity.CupsCatalogEntity
import android.waterreminder.data.entity.WaterHistoryEntity
import kotlinx.coroutines.flow.Flow
import java.util.Calendar
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
     */
    fun getTodayHistoryLogs(): Flow<List<WaterHistoryEntity>> {
        val startOfTodayTimestamp = getStartOfTodayTimestamp()
        return dashboardDao.getTodayHistoryFlow(startOfTodayTimestamp)
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
     * Deletes a water consumption entry from the history table.
     */
    suspend fun deleteWaterLog(log: WaterHistoryEntity) {
        dashboardDao.deleteLog(log)
    }


    // --- Internal Time Math Helper Logic ---

    /**
     * Computes the exact Unix Epoch millisecond timestamp representing 12:00:00 AM of the current local day.
     */
    private fun getStartOfTodayTimestamp(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
}