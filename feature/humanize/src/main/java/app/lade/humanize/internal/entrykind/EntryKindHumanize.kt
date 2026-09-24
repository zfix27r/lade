package app.lade.humanize.internal.entrykind

import android.content.Context
import app.lade.entrykind.EntryKind
import app.lade.entrykind.labelRes
import app.lade.humanize.api.Humanized

internal class EntryKindHumanize(
    private val context: Context,
) {
    fun format(kind: EntryKind): Humanized {
        val text = context.getString(kind.labelRes())
        return Humanized(short = text, long = text)
    }
}