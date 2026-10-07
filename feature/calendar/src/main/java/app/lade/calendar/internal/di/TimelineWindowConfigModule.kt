package app.lade.calendar.internal.di

import app.lade.calendar.api.config.DefaultTimelineWindowConfig
import app.lade.calendar.api.config.TimelineWindowConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object TimelineWindowConfigModule {

    @Provides
    @Singleton
    internal fun provideTimelineWindowConfig(): TimelineWindowConfig =
        DefaultTimelineWindowConfig
}