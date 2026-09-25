package app.lade.draft.internal.di

import app.lade.draft.api.DraftApi
import app.lade.draft.internal.DraftApiImpl
import app.lade.draft.internal.data.DraftRepository
import app.lade.draft.internal.domain.DraftStore
import app.lade.draft.internal.ui.bar.BarStateHolder
import app.lade.entrykind.EntryKindResolver
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@Module
@InstallIn(ActivityRetainedComponent::class)
internal object DraftProvidesModule {

    @Provides
    @ActivityRetainedScoped
    fun provideScope(): CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    @Provides
    @ActivityRetainedScoped
    fun provideBarStateHolder(): BarStateHolder = BarStateHolder()

    @Provides
    @ActivityRetainedScoped
    fun provideDraftStore(
        repository: DraftRepository,
        kindResolver: EntryKindResolver,
        scope: CoroutineScope,
    ): DraftStore = DraftStore(repository, kindResolver, scope)
}

@Module
@InstallIn(ActivityRetainedComponent::class)
internal abstract class DraftBindsModule {

    @Binds
    @ActivityRetainedScoped
    abstract fun bindDraftApi(impl: DraftApiImpl): DraftApi
}