package app.lade.entrydetailsscreen.domain

import app.lade.agenda.api.entry.EntryModel
import app.lade.entry.EntryKind

data class EntryListUiState(
    val items: List<EntryModel> = emptyList(),
    val entryKindFilter: EntryKind? = null,
    val showArchived: Boolean = false,
)
