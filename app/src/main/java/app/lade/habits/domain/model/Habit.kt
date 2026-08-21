package app.lade.habits.domain.model

data class Habit(
	val id: Long = 0,
	val title: String,
	val categoryId: Long,
	val rrule: String,
	val goalValue: Double,
	val goalUnit: String,
	val goalType: String = "distance",
	val timeOfDayMinutes: Int? = null,
	val alarmMode: String = "none",
	val reminderMinutesBefore: Int? = null,
)

object HabitGoalPresets {
	/** Storage codes; labels — `R.string.habit_unit_*` in UI. */
	val UNIT_CODES = listOf("km", "min", "reps")
}
