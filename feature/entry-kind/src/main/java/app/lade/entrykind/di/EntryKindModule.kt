package app.lade.entrykind.di

import app.lade.entrykind.EntryKindResolver
import app.lade.entrykind.internal.EntryKindResolverImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class EntryKindModule {

    @Binds
    @Singleton
    internal abstract fun bindEntryKindResolver(
        impl: EntryKindResolverImpl,
    ): EntryKindResolver
}