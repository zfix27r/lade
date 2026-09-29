package app.lade.draftdata

data class DraftNote(
    val id: Long = 0,
    val text: String = "",
    val createdAtEpochMs: Long = 0,
)