package app.lade.habits.ui.mark

import java.time.LocalDate

/** One-shot snackbar payload after Done/Skip. */
data class HabitMarkUndoEvent(
	val habitId: Long,
	val habitTitle: String,
	val dateEpochDay: Long,
	val newResult: String,
	val previousResult: String?,
	val nonce: Long = System.nanoTime(),
) {
	val date: LocalDate get() = LocalDate.ofEpochDay(dateEpochDay)
}
