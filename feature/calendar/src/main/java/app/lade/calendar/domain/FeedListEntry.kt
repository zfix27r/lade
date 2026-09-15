package app.lade.calendar.domain
import androidx.annotation.StringRes

sealed class FeedListEntry {
    data class DayHeader(val dateEpochDay: Long) : FeedListEntry()
    data class Row(val item: FeedItem) : FeedListEntry()
}


data class FeedFilterOption(
    val source: FeedSource?,
    @StringRes val labelRes: Int,
)