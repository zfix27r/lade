package app.lade.habits.domain.model

data class HabitHistory(
	val id: Long = 0,
	val habitId: Long,
	val dateEpochDay: Long,
	val title: String,
	val categoryId: Long,
	val goalType: String,
	val goalValue: Double,
	val goalUnit: String,
	val timeOfDayMinutes: Int? = null,
	val result: String? = null,
	val actualValue: Double? = null,
	val notedAtEpochMs: Long? = null,
	val source: String? = null,
	val timeBlockId: Long? = null,
)
