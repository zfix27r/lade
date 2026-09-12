package app.lade.entry.di

import app.lade.entry.data.EntryHistoryRepositoryImpl
import app.lade.entry.data.EntryRepositoryImpl
import app.lade.entry.data.KindPriorityStore
import app.lade.entry.domain.EntryHistoryRepository
import app.lade.entry.domain.EntryRepository
import app.lade.entry.domain.KindPriorityRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class EntryDataModule {
	@Binds
	@Singleton
	abstract fun bindEntryRepository(impl: EntryRepositoryImpl): EntryRepository

	@Binds
	@Singleton
	abstract fun bindEntryHistoryRepository(impl: EntryHistoryRepositoryImpl): EntryHistoryRepository

	@Binds
	@Singleton
	abstract fun bindKindPriorityRepository(impl: KindPriorityStore): KindPriorityRepository
}