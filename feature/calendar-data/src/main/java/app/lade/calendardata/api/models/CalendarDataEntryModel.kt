package app.lade.calendardata.api.models

import app.lade.entry.EntryKind
import java.time.LocalTime

data class CalendarDataEntryModel(
    val id: Long,
    val kind: EntryKind,
    val title: String,
    val timeFrom: LocalTime?,
    val timeTo: LocalTime?,
)