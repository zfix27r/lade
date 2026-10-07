package app.lade.calendar.internal.week.data

import java.time.LocalTime

internal fun coversHour(start: LocalTime, end: LocalTime, hour: Int): Boolean {
    val hourStart = hour * 60
    val hourEnd = hourStart + 60
    val startMin = start.hour * 60 + start.minute
    val endMin = end.hour * 60 + end.minute
    return startMin < hourEnd && endMin > hourStart
}

internal const val GRID_HOUR_FROM = 6
internal const val GRID_HOUR_TO = 22
