package app.lade.temporal.domain

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
