package app.lade.draft.internal.input

internal sealed interface BarInputSubmit {
    data object None : BarInputSubmit
    data object Send : BarInputSubmit
    data object Commit : BarInputSubmit
}