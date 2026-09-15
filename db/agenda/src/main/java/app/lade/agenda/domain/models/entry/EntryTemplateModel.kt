package app.lade.agenda.domain.models.entry

import app.lade.agenda.api.entry.EntryKind

data class EntryTemplateModel(
    val id: Long,
    val title: String,
    val kind: EntryKind,
    val colorRes: String? = null,
    val iconRes: String? = null,
)