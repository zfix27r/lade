package app.lade.humanize.internal.entrykind

import android.content.Context
import app.lade.entry.EntryKind
import app.lade.entry.ui.label
import app.lade.humanize.api.Humanized

internal class EntryKindHumanize(
    private val context: Context,
) {
    fun format(entryKind: EntryKind): Humanized {
        val text = context.getString(entryKind.label())
        return Humanized(short = text, long = text)
    }
}