package app.lade.calendar.internal.card

import app.lade.calendardata.api.models.CalendarDataEntryModel

fun CalendarDataEntryModel.timeRangeString(): String = buildString {
    timeFrom?.let { append(it) }
    timeTo?.let { append("–").append(it) }
}