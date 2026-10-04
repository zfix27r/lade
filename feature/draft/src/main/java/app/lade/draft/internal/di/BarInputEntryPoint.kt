package app.lade.draft.internal.di

import app.lade.draft.internal.input.domain.BarInputControlPort
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
internal interface BarInputEntryPoint {
    fun barInputControlPort(): BarInputControlPort
}