package app.lade.recurrence.di

import app.lade.recurrence.api.RecurrenceEngine
import app.lade.recurrence.data.RecurrenceEngineBiweekly
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TemporalDataModule {
	@Binds
	@Singleton
	abstract fun bindScheduleEngine(impl: RecurrenceEngineBiweekly): RecurrenceEngine
}