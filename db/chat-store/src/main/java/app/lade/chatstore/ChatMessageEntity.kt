package app.lade.chatstore

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
	@PrimaryKey(autoGenerate = true) val id: Long = 0,
	val kind: String,
	val text: String = "",
	val messageKey: String? = null,
	val detail: String? = null,
	val createdAtEpochMs: Long,
)
