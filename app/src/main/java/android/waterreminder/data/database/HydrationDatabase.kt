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
    exportSchema = false // TODO: Keeps project build outputs clean for now (set it later)
)
abstract class HydrationDatabase : RoomDatabase() {

    abstract fun dashboardDao(): DashboardDao

    companion object {
        @Volatile
        private var INSTANCE: HydrationDatabase? = null

        // Singleton pattern
        fun getDatabase(context: Context, scope: CoroutineScope): HydrationDatabase {
            return INSTANCE ?: synchronized(this) {
                // We create a reference to the builder structure first
                var instance: HydrationDatabase? = null

                val builder = Room.databaseBuilder(
                    context.applicationContext,
                    klass = HydrationDatabase::class.java,
                    name = "hydration_database"
                ).addCallback(
                    object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Run pre-population safely using the freshly built instance reference
                            scope.launch(Dispatchers.IO) {
                                instance?.dashboardDao()?.let { dao ->
                                    CupsCatalogEntity.DEFAULT_PRESET_CUPS.forEach { amount ->
                                        dao.insertCup(CupsCatalogEntity(amountMl = amount))
                                    }
                                }
                            }
                        }
                    }
                )

                instance = builder.build()
                INSTANCE = instance
                instance
            }
        }
    }
}