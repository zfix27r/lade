package app.lade.calendar.domain

data class FeedItem(
    val key: String,
    val dateEpochDay: Long,
    val title: String,
    val source: FeedSource,
    val blockId: Long? = null,
    val entryId: Long? = null,
    val startMinutes: Int? = null,
    val endMinutes: Int? = null,
    val habitId: Long? = null,
    val habitDone: Boolean = false,
    val habitTimeMinutes: Int? = null,
    val habitGoalValue: String? = null,
    val habitGoalUnit: String? = null,
    val sortMinutes: Int,
)