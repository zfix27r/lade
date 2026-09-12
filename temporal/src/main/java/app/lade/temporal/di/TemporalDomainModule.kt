package app.lade.temporal.di

import app.lade.temporal.data.BiweeklyScheduleEngine
import app.lade.temporal.domain.ScheduleEngine
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TemporalDomainModule {
	@Provides
	@Singleton
	fun provideScheduleEngine(): ScheduleEngine = BiweeklyScheduleEngine()
}