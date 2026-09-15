package app.lade.chatstore

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_dicts")
data class ChatDictEntity(
	@PrimaryKey(autoGenerate = true) val id: Long = 0,
	val title: String,
	/** [app.lade.agenda.domain.models.EntryKind.storage] */
	val kind: String,
	val phrasesCsv: String,
	val systemKey: String? = null,
	val archivedAtEpochMs: Long? = null,
)
