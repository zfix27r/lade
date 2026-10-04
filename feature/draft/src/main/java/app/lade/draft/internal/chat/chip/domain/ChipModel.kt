package app.lade.draft.internal.chat.chip.domain

internal data class ChipModel(
    val data: ChipData,
    val state: ChipState,
    val emphasis: ChipEmphasis,
)