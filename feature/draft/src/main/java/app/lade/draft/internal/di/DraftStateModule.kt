package app.lade.draft.internal.di

import app.lade.draft.internal.ui.bar.DraftStateHolder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DraftStateModule {
    @Provides
    @Singleton
    fun provideDraftStateHolder(): DraftStateHolder = DraftStateHolder()
}