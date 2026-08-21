package app.lade.time.data

import app.lade.time.domain.TimeBlockRepository
import app.lade.time.domain.TimeScheduleRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TimeDataModule {
	@Binds
	@Singleton
	abstract fun bindTimeScheduleRepository(impl: TimeScheduleRepositoryImpl): TimeScheduleRepository

	@Binds
	@Singleton
	abstract fun bindTimeBlockRepository(impl: TimeBlockRepositoryImpl): TimeBlockRepository
}

