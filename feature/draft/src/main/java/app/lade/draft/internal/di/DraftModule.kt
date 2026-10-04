package app.lade.draft.internal.di

import app.lade.draft.api.DraftApi
import app.lade.draft.internal.DraftApiImpl
import app.lade.draft.internal.data.DraftRepository
import app.lade.draft.internal.domain.DraftStore
import app.lade.draft.internal.ui.bar.DraftStateHolder
import app.lade.entry.EntryKindResolver
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DraftProvidesModule {
    @Provides
    @Singleton
    fun provideScope(): CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    @Provides
    @Singleton
    fun provideDraftStore(
        repository: DraftRepository,
        kindResolver: EntryKindResolver,
        scope: CoroutineScope,
    ): DraftStore = DraftStore(repository, kindResolver, scope)
}

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DraftBindsModule {

    @Binds
    @Singleton
    abstract fun bindDraftApi(impl: DraftApiImpl): DraftApi
}