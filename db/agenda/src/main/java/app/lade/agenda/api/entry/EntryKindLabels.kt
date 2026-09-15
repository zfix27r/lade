package app.lade.agenda.api.entry

import androidx.annotation.StringRes
import app.lade.resources.R

@StringRes
fun EntryKind.labelRes(): Int = when (this) {
	EntryKind.TASK -> R.string.entry_kind_task
	EntryKind.EVENT -> R.string.entry_kind_event
	EntryKind.HABIT -> R.string.entry_kind_habit
	EntryKind.SCHEDULE -> R.string.entry_kind_schedule
	EntryKind.UNKNOWN -> R.string.entry_kind_unknown
}
