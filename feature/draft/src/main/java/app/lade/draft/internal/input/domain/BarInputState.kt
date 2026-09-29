package app.lade.draft.internal.input.domain

internal data class BarInputState(
    val action: BarInputAction? = null,
    val actionEnabled: Boolean = false,
)