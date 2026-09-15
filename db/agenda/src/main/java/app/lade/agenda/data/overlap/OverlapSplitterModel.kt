package app.lade.agenda.data.overlap

import app.lade.agenda.api.entry.EntryModel

data class OverlapSplitterModel(
    val primary: EntryModel,
    val secondary: EntryModel?,
)