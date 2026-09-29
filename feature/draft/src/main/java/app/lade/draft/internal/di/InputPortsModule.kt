package app.lade.draft.internal.di

import app.lade.draft.internal.input.InputInPort
import app.lade.draft.internal.input.InputOutPort
import app.lade.draft.internal.input.domain.BarInputHolder
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface InputPortsModule {

    @Binds
    @Singleton
    fun bindInputInPort(impl: BarInputHolder): InputInPort

    @Binds
    @Singleton
    fun bindInputOutPort(impl: BarInputHolder): InputOutPort
}