package app.lade.draft.internal.chat.input

internal object InputClearRules {
    fun shouldClear(reason: InputClearReason): Boolean = when (reason) {
        InputClearReason.Save -> true
        InputClearReason.Reset -> true
        InputClearReason.EditExit -> true
        is InputClearReason.RemoveEditing -> true
    }
}