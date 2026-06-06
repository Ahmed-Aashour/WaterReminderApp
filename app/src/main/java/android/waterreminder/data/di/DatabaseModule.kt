package android.waterreminder.data.di

import android.content.Context
import android.waterreminder.data.dao.DashboardDao
import android.waterreminder.data.database.HydrationDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideCoroutineScope(): CoroutineScope {
        // Global scope tied to the application lifetime for background DB operations
        return CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }

    @Provides
    @Singleton
    fun provideHydrationDatabase(
        @ApplicationContext context: Context,
        scope: CoroutineScope
    ): HydrationDatabase {
        // Singleton database instance using the companion builder
        return HydrationDatabase.getDatabase(context, scope)
    }

    @Provides
    @Singleton
    fun provideDashboardDao(database: HydrationDatabase): DashboardDao {
        // DAO using the initialized database instance
        return database.dashboardDao()
    }
}