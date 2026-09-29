package app.lade.agenda.api.note

data class NoteModel(
    val id: Long = 0,
    val entryId: Long,
    val text: String,
    val createdAtEpochMs: Long,
    val updatedAtEpochMs: Long? = null,
)