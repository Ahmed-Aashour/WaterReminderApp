package android.waterreminder.data.database

import android.content.Context
import android.waterreminder.data.dao.DashboardDao
import android.waterreminder.data.entity.CupsCatalogEntity
import android.waterreminder.data.entity.WaterHistoryEntity
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [WaterHistoryEntity::class, CupsCatalogEntity::class],
    version = 1,
    exportSchema = false // Keeps project build outputs clean for now
)
abstract class HydrationDatabase : RoomDatabase() {

    // Expose your feature DAOs here
    abstract fun dashboardDao(): DashboardDao

    companion object {
        @Volatile
        private var INSTANCE: HydrationDatabase? = null

        // Singleton pattern: ensures only one instance of the database is created across the app
        fun getDatabase(context: Context, scope: CoroutineScope): HydrationDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HydrationDatabase::class.java,
                    "hydration_database" // The actual filename on disk
                )
                    .addCallback(HydrationDatabaseCallback(scope)) // Attaches our default data loader
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }

    // A callback class to run tasks when the database events occur
    private class HydrationDatabaseCallback(
        private val scope: CoroutineScope
    ) : Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // onCreate triggers ONLY the very first time the app runs and creates the database tables
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDefaultCupsCatalog(database.dashboardDao())
                }
            }
        }

        // Seeds your database with your initial custom cup sizes catalog profile
        suspend fun populateDefaultCupsCatalog(dashboardDao: DashboardDao) {
            dashboardDao.insertCup(CupsCatalogEntity(amountMl = 250))
            dashboardDao.insertCup(CupsCatalogEntity(amountMl = 350))
            dashboardDao.insertCup(CupsCatalogEntity(amountMl = 500))
        }
    }
}