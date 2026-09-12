package app.lade.calendar.domain

data class FeedItem(
    val key: String,
    val dateEpochDay: Long,
    val title: String,
    val source: String,
    val blockId: Long? = null,
    val entryId: Long? = null,
    val startMinutes: Int? = null,
    val endMinutes: Int? = null,
    val habitId: Long? = null,
    val habitResult: String? = null,
    val habitGoalValue: Double? = null,
    val habitGoalUnit: String? = null,
    val habitTimeMinutes: Int? = null,
    val sortMinutes: Int,
)