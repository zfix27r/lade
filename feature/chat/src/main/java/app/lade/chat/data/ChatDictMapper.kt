package app.lade.chat.data

import app.lade.chat.domain.model.ChatDictEntry
import app.lade.chatstore.ChatDictEntity
import app.lade.entrykind.EntryKind

fun ChatDictEntity.toDomain() = ChatDictEntry(
	id = id,
	title = title,
	kind = EntryKind.valueOf(kind),
	phrases = phrasesCsv.split(',').map { it.trim() }.filter { it.isNotEmpty() },
	systemKey = systemKey,
	archivedAtEpochMs = archivedAtEpochMs,
)

fun ChatDictEntry.toEntity() = ChatDictEntity(
	id = id,
	title = title,
	kind = kind.storage,
	phrasesCsv = phrases.joinToString(",") { it.trim().lowercase() }.trim(','),
	systemKey = systemKey,
	archivedAtEpochMs = archivedAtEpochMs,
)
