package app.lade.entrydetailsscreen.di

import app.lade.entrydetailsscreen.data.EntryEditRepositoryImpl
import app.lade.entrydetailsscreen.data.EntryListRepository
import app.lade.entrydetailsscreen.data.EntryListRepositoryImpl
import app.lade.entrydetailsscreen.domain.EntryEditRepository
import app.lade.entrydetailsscreen.domain.mapper.EntryEditMapper
import app.lade.entrydetailsscreen.domain.resolver.EntryKindResolver
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class EntryDetailsModule {

    @Binds
    @Singleton
    abstract fun bindEntryEditRepository(impl: EntryEditRepositoryImpl): EntryEditRepository

    @Binds
    @Singleton
    abstract fun bindEntryListRepository(impl: EntryListRepositoryImpl): EntryListRepository

    companion object {
        @Provides
        @Singleton
        fun provideEntryKindResolver(): EntryKindResolver = EntryKindResolver()

        @Provides
        @Singleton
        fun provideEntryEditMapper(resolver: EntryKindResolver): EntryEditMapper =
            EntryEditMapper(resolver)
    }
}