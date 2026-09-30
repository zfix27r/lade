package app.lade.draft.internal.chat.chip.domain

internal data class ChipStates(
    val editingKey: ChipKey? = null,
    val proposedKeys: Set<ChipKey> = emptySet(),
) {
    val hasActive: Boolean
        get() = editingKey != null || proposedKeys.isNotEmpty()

    fun stateOf(key: ChipKey): ChipState = when {
        editingKey == key -> ChipState.EDITING
        key in proposedKeys -> ChipState.PROPOSED
        else -> ChipState.COMMITTED
    }

    fun emphasisOf(key: ChipKey): ChipEmphasis {
        if (!hasActive) return ChipEmphasis.ACTIVE
        return if (editingKey == key || key in proposedKeys) {
            ChipEmphasis.ACTIVE
        } else {
            ChipEmphasis.MUTED
        }
    }
}