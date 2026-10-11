package app.lade.draft.internal.di

import app.lade.draft.internal.chat.chip.ChipInsidePort
import app.lade.draft.internal.chat.chip.ChipOutsidePort
import app.lade.draft.internal.chat.chip.ChipPortImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class ChipModule {

    @Binds
    @Singleton
    abstract fun bindChipOutPort(impl: ChipPortImpl): ChipOutsidePort

    @Binds
    @Singleton
    abstract fun bindChipControlPort(impl: ChipPortImpl): ChipInsidePort
}