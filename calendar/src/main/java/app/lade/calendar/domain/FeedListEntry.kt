package app.lade.calendar.domain

sealed class FeedListEntry {
    data class DayHeader(val dateEpochDay: Long) : FeedListEntry()
    data class Row(val item: FeedItem) : FeedListEntry()
}