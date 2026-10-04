package app.lade.calendardata.internal.di

import app.lade.calendardata.api.CalendarDataApi
import app.lade.calendardata.internal.CalendarDataApiImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class CalendarDataModule {

    @Binds
    @Singleton
    internal abstract fun bindCalendarDataApi(
        impl: CalendarDataApiImpl,
    ): CalendarDataApi
}