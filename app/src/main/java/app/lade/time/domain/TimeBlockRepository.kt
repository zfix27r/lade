package app.lade.time.domain

import app.lade.time.domain.model.TimeBlock
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface TimeBlockRepository {
	fun observeByDate(date: LocalDate): Flow<List<TimeBlock>>
	fun observeBetween(fromInclusive: LocalDate, toInclusive: LocalDate): Flow<List<TimeBlock>>
	suspend fun getByDate(date: LocalDate): List<TimeBlock>
	suspend fun getBetween(fromInclusive: LocalDate, toInclusive: LocalDate): List<TimeBlock>
	suspend fun getById(id: Long): TimeBlock?
	suspend fun getByScheduleFrom(scheduleId: Long, fromInclusive: LocalDate): List<TimeBlock>
	suspend fun save(block: TimeBlock): Long
	/** Insert or update by unique (scheduleId, date); for schedule-sourced blocks. */
	suspend fun upsertByScheduleDate(block: TimeBlock): Long
	suspend fun delete(id: Long)
	suspend fun deleteByScheduleFrom(scheduleId: Long, fromInclusive: LocalDate)
}
