package app.lade.habits.data

import app.lade.habits.domain.HabitHistoryRepository
import app.lade.habits.domain.HabitRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class HabitsDataModule {
	@Binds
	@Singleton
	abstract fun bindHabitRepository(impl: HabitRepositoryImpl): HabitRepository

	@Binds
	@Singleton
	abstract fun bindHabitHistoryRepository(impl: HabitHistoryRepositoryImpl): HabitHistoryRepository
}
