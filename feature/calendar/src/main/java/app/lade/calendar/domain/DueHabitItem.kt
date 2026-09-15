package app.lade.calendar.domain

data class DueHabitItem(
	val entryId: Long,
	val title: String,
	val goalLabel: String?,
	val timeOfDayMinutes: Int?,
	val done: Boolean,
)