package app.lade.draft.internal.di

import app.lade.draft.internal.chat.chip.ChipControlPort
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
internal interface ChipEntryPoint {
    fun chipControlPort(): ChipControlPort
}