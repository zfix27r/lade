package app.lade.draft.di

import app.lade.draft.DraftApi
import app.lade.draft.internal.DraftApiImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DraftModule {

    @Binds
    internal abstract fun bindDraftApi(impl: DraftApiImpl): DraftApi
}