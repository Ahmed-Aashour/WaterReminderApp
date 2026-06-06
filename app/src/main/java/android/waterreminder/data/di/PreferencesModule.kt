package android.waterreminder.data.di

import android.content.Context
import android.waterreminder.data.store.AppSettingsDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {

    /**
     * Provides a thread-safe, single-instance engine wrapper managing global application
     * preferences, key-value storage files, and structural runtime mutation channels.
     */
    @Provides
    @Singleton
    fun provideAppSettingsDataStore(
        @ApplicationContext context: Context
    ): AppSettingsDataStore {
        return AppSettingsDataStore(context)
    }
}