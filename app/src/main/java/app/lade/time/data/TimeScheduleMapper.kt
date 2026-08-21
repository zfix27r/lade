package app.lade.time.data

import app.lade.database.entity.TimeBlockEntity
import app.lade.database.entity.TimeScheduleEntity
import app.lade.time.domain.model.TimeBlock
import app.lade.time.domain.model.TimeSchedule
import java.time.LocalDate
import java.time.LocalTime

fun TimeScheduleEntity.toDomain() = TimeSchedule(
	id = id,
	title = title,
	categoryId = categoryId,
	dateFrom = LocalDate.ofEpochDay(dateFromEpochDay),
	dateTo = LocalDate.ofEpochDay(dateToEpochDay),
	rrule = rrule,
	startTime = LocalTime.ofSecondOfDay(startTimeMinutes * 60L),
	endTime = LocalTime.ofSecondOfDay(endTimeMinutes * 60L),
)

fun TimeSchedule.toEntity() = TimeScheduleEntity(
	id = id,
	title = title,
	categoryId = categoryId,
	dateFromEpochDay = dateFrom.toEpochDay(),
	dateToEpochDay = dateTo.toEpochDay(),
	rrule = rrule,
	startTimeMinutes = startTime.hour * 60 + startTime.minute,
	endTimeMinutes = endTime.hour * 60 + endTime.minute,
)

fun TimeBlockEntity.toDomain() = TimeBlock(
	id = id,
	date = LocalDate.ofEpochDay(dateEpochDay),
	start = LocalTime.ofSecondOfDay(startTimeMinutes * 60L),
	end = LocalTime.ofSecondOfDay(endTimeMinutes * 60L),
	categoryId = categoryId,
	title = title,
	source = source,
	scheduleId = scheduleId,
	habitHistoryId = habitHistoryId,
	healthSampleId = healthSampleId,
	calendarEventId = calendarEventId,
	locked = locked,
)

fun TimeBlock.toEntity() = TimeBlockEntity(
	id = id,
	dateEpochDay = date.toEpochDay(),
	startTimeMinutes = start.hour * 60 + start.minute,
	endTimeMinutes = end.hour * 60 + end.minute,
	categoryId = categoryId,
	title = title,
	source = source,
	scheduleId = scheduleId,
	habitHistoryId = habitHistoryId,
	healthSampleId = healthSampleId,
	calendarEventId = calendarEventId,
	locked = locked,
)

