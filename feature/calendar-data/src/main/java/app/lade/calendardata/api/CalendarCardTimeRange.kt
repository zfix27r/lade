package app.lade.calendardata.api

fun CalendarCardModel.timeRangeString(): String = buildString {
    timeFrom?.let { append(it) }
    timeTo?.let { append("–").append(it) }
}