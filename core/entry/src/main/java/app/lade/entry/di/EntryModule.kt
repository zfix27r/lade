package app.lade.entry.di

import app.lade.entry.EntryKindResolver
import app.lade.entry.internal.EntryKindResolverImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class EntryModule {

    @Binds
    @Singleton
    internal abstract fun bindEntryResolver(impl: EntryKindResolverImpl): EntryKindResolver
}