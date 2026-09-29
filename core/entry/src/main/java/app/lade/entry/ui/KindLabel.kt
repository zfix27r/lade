package app.lade.entry.ui

import androidx.annotation.StringRes
import app.lade.entry.EntryKind
import app.lade.entry.R

@StringRes
fun EntryKind.label(): Int = when (this) {
    EntryKind.NOTE -> R.string.entry_kind_note
    EntryKind.TASK -> R.string.entry_kind_task
    EntryKind.EVENT -> R.string.entry_kind_event
    EntryKind.HABIT -> R.string.entry_kind_habit
    EntryKind.SCHEDULE -> R.string.entry_kind_schedule
}