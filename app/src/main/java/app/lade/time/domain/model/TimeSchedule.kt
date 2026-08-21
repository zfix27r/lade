package app.lade.time.domain.model

import java.time.LocalDate
import java.time.LocalTime

data class TimeSchedule(
	val id: Long = 0,
	val title: String,
	val categoryId: Long,
	val dateFrom: LocalDate,
	val dateTo: LocalDate,
	val rrule: String,
	val startTime: LocalTime,
	val endTime: LocalTime,
)
