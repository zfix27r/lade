package app.lade.agenda.data.overlap

import app.lade.agenda.api.entry.EntryModel

data class OverlapSplitResult(
    val primary: EntryModel,
    val secondary: EntryModel?,
)