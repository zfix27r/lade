package app.lade.draft.internal.input

internal interface InputOutPort {
    fun onValueChange(text: String)
    fun onActionClick()
    fun observe(block: (String) -> Unit)
    fun observeAction(block: () -> Unit)
    fun clear()
}