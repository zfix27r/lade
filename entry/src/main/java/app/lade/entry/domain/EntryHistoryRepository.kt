package app.lade.entry.domain

import app.lade.entry.domain.models.EntryHistory
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface EntryHistoryRepository {
	fun observeByDate(date: LocalDate): Flow<List<EntryHistory>>
	fun observeBetween(fromInclusive: LocalDate, toInclusive: LocalDate): Flow<List<EntryHistory>>
	suspend fun save(history: EntryHistory): Long
}
