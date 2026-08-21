package app.lade.time.domain.model

import java.time.LocalDate
import java.time.LocalTime

data class TimeBlock(
	val id: Long = 0,
	val date: LocalDate,
	val start: LocalTime,
	val end: LocalTime,
	val categoryId: Long,
	val title: String? = null,
	val source: String,
	val scheduleId: Long? = null,
	val habitHistoryId: Long? = null,
	val healthSampleId: Long? = null,
	val calendarEventId: String? = null,
	val locked: Boolean = false,
)
