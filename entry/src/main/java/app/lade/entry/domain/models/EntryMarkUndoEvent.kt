package app.lade.entry.domain.models

import java.time.LocalDate

data class EntryMarkUndoEvent(
    val entryId: Long,
    val entryTitle: String,
    val dateEpochDay: Long,
    val newResult: String,
    val previousResult: String?,
    val nonce: Long = System.nanoTime(),
) {
    val date: LocalDate get() = LocalDate.ofEpochDay(dateEpochDay)
}