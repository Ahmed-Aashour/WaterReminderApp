package android.waterreminder.data.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class TimeFormat12Hour

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class TimeFormat24Hour

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DateFormatShort