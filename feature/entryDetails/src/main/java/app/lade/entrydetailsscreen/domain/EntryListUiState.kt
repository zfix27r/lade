package app.lade.entrydetailsscreen.domain

import app.lade.agenda.api.entry.EntryKind
import app.lade.agenda.api.entry.EntryModel

data class EntryListUiState(
    val items: List<EntryModel> = emptyList(),
    val kindFilter: EntryKind? = null,
    val showArchived: Boolean = false,
)
