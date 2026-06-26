package android.waterreminder.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DateTimeModule {

    @Provides
    @Singleton
    @TimeFormat12Hour
    fun provide12HourTimeFormatter(): DateTimeFormatter {
        return DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault())
    }

    @Provides
    @Singleton
    @TimeFormat24Hour
    fun provide24HourTimeFormatter(): DateTimeFormatter {
        return DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
    }

    @Provides
    @Singleton
    @DateFormatShort
    fun provideShortDateFormatter(): DateTimeFormatter {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
    }
}