package app.lade.time.domain

import app.lade.time.domain.model.TimeSchedule
import kotlinx.coroutines.flow.Flow

interface TimeScheduleRepository {
	fun observeActive(): Flow<List<TimeSchedule>>
	suspend fun getById(id: Long): TimeSchedule?
	suspend fun save(schedule: TimeSchedule): Long
	suspend fun delete(id: Long)
}
