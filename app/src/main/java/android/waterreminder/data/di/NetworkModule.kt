package android.waterreminder.data.di

import android.waterreminder.service.remote.AladhanApiService
import android.waterreminder.data.repository.PrayerTimesRepositoryImpl
import android.waterreminder.data.repository.PrayerTimesRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {

    // Step 1: Bind the abstraction interface to the concrete implementation class
    @Binds
    @Singleton
    abstract fun bindPrayerTimesRepository(
        prayerTimesRepositoryImpl: PrayerTimesRepositoryImpl
    ): PrayerTimesRepository

    companion object {
        private const val BASE_URL = "https://api.aladhan.com/v1/"

        // Step 2: Provide the AladhanApiService retrofit instance needed by the constructor graph
        @Provides
        @Singleton
        fun provideAladhanApiService(): AladhanApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(AladhanApiService::class.java)
        }
    }
}