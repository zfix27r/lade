package app.lade.draft.internal.di

import app.lade.draft.internal.input.domain.BarInputControlPort
import app.lade.draft.internal.input.BarInputPort
import app.lade.draft.internal.input.BarInputPortImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface BarInputModule {

    @Binds
    fun bindBarInputPort(impl: BarInputPortImpl): BarInputPort

    @Binds
    fun bindBarInputControlPort(impl: BarInputPortImpl): BarInputControlPort
}