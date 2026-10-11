package app.lade.calendar.internal.list.card.expandable

import androidx.compose.runtime.Immutable

@Immutable
internal data class CardExpandableKey(
    val entryId: Long,
    val epochDay: Long,
) {
    val asString: String = "expandable-$entryId-$epochDay"

    companion object {
        fun of(entryId: Long, epochDay: Long): CardExpandableKey =
            CardExpandableKey(entryId = entryId, epochDay = epochDay)
    }
}