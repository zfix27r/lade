package app.lade.calendar.ui

/**
 * Feed entry source keys (filter chips). Habit marks are a separate source from TimeBlock.
 */
object FeedSource {
	const val SCHEDULE = "schedule"
	const val MANUAL = "manual"
	const val HABIT = "habit"
	const val HEALTH = "health"
	const val CALENDAR = "calendar"
}

data class FeedItem(
	val key: String,
	val dateEpochDay: Long,
	val title: String,
	val source: String,
	val blockId: Long? = null,
	val startMinutes: Int? = null,
	val endMinutes: Int? = null,
	val habitId: Long? = null,
	val habitResult: String? = null,
	val habitGoalValue: Double? = null,
	val habitGoalUnit: String? = null,
	val habitTimeMinutes: Int? = null,
	/** Minutes from midnight for ordering within a day; habits without time use end-of-day. */
	val sortMinutes: Int,
)

sealed class FeedListEntry {
	data class DayHeader(val dateEpochDay: Long) : FeedListEntry()
	data class Row(val item: FeedItem) : FeedListEntry()
}
