package app.lade.calendar.internal.di

import app.lade.calendar.api.config.DefaultStripConfig
import app.lade.calendar.api.config.StripConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object StripConfigModule {

    @Provides
    @Singleton
    internal fun provideStripConfig(): StripConfig = DefaultStripConfig
}