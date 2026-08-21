package app.lade.habits.data

import app.lade.database.entity.HabitEntity
import app.lade.database.entity.HabitHistoryEntity
import app.lade.habits.domain.model.Habit
import app.lade.habits.domain.model.HabitHistory

fun HabitEntity.toDomain() = Habit(
	id = id,
	title = title,
	categoryId = categoryId,
	rrule = rrule,
	goalValue = goalValue,
	goalUnit = goalUnit,
	goalType = goalType,
	timeOfDayMinutes = timeOfDayMinutes,
	alarmMode = alarmMode,
	reminderMinutesBefore = reminderMinutesBefore,
)

fun Habit.toEntity(createdAtEpochMs: Long) = HabitEntity(
	id = id,
	title = title,
	categoryId = categoryId,
	rrule = rrule,
	goalValue = goalValue,
	goalUnit = goalUnit,
	goalType = goalType,
	timeOfDayMinutes = timeOfDayMinutes,
	alarmMode = alarmMode,
	reminderMinutesBefore = reminderMinutesBefore,
	createdAtEpochMs = createdAtEpochMs,
)

fun HabitHistoryEntity.toDomain() = HabitHistory(
	id = id,
	habitId = habitId,
	dateEpochDay = dateEpochDay,
	title = title,
	categoryId = categoryId,
	goalType = goalType,
	goalValue = goalValue,
	goalUnit = goalUnit,
	timeOfDayMinutes = timeOfDayMinutes,
	result = result,
	actualValue = actualValue,
	notedAtEpochMs = notedAtEpochMs,
	source = source,
	timeBlockId = timeBlockId,
)

fun HabitHistory.toEntity() = HabitHistoryEntity(
	id = id,
	habitId = habitId,
	dateEpochDay = dateEpochDay,
	title = title,
	categoryId = categoryId,
	goalType = goalType,
	goalValue = goalValue,
	goalUnit = goalUnit,
	timeOfDayMinutes = timeOfDayMinutes,
	result = result,
	actualValue = actualValue,
	notedAtEpochMs = notedAtEpochMs,
	source = source,
	timeBlockId = timeBlockId,
)
