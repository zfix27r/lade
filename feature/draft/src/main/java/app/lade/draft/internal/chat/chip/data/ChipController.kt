package app.lade.draft.internal.chat.chip.data

internal class ChipController {

    fun onPropose(states: ChipStates, keys: Set<ChipKey>): ChipStates {
        if (keys.isEmpty()) return states
        return states.copy(proposedKeys = states.proposedKeys + keys)
    }

    fun onTap(states: ChipStates, key: ChipKey): ChipStates =
        when (states.stateOf(key)) {
            ChipState.PROPOSED -> states.copy(proposedKeys = states.proposedKeys - key)
            ChipState.EDITING -> states.copy(editingKey = null)
            ChipState.COMMITTED -> states.copy(editingKey = key, proposedKeys = emptySet())
            ChipState.NONE -> states
        }

    fun onRemove(states: ChipStates, key: ChipKey): ChipStates =
        states.copy(
            editingKey = if (states.editingKey == key) null else states.editingKey,
            proposedKeys = states.proposedKeys - key,
        )

    fun onCommitAll(states: ChipStates): ChipStates =
        states.copy(proposedKeys = emptySet(), editingKey = null)

    fun onClear(states: ChipStates): ChipStates = ChipStates()
}