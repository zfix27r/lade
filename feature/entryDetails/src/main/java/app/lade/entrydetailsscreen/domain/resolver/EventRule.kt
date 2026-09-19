package app.lade.entrydetailsscreen.domain.resolver

import app.lade.entrykind.EntryKind

/** Время-диапазон в один день (без диапазона дат) → EVENT. */
object EventRule : EntryKindRule {
    override fun resolve(input: EntryKindInput): EntryKind? =
        if (input.hasTimeRange) EntryKind.EVENT else null
}