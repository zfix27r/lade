package app.lade.entry.domain

import app.lade.entry.domain.models.EntryKind
import kotlinx.coroutines.flow.Flow

interface KindPriorityRepository {
	val order: Flow<List<EntryKind>>
	fun current(): List<EntryKind>
	suspend fun setOrder(kinds: List<EntryKind>)
}
