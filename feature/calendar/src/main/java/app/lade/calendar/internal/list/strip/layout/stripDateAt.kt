package app.lade.calendar.internal.list.strip.layout

import java.time.LocalDate

internal fun stripDateAt(
    weeks: List<List<LocalDate>>,
    x: Float,
    y: Float,
    widthPx: Int,
    columnOffsetPx: Float,
    rowHeightPx: Float,
): LocalDate? {
    val cellWidth = widthPx / 7f
    if (cellWidth <= 0f) return null
    val col = (x / cellWidth).toInt().coerceIn(0, 6)
    val row = ((y - columnOffsetPx) / rowHeightPx).toInt()
    return weeks.getOrNull(row)?.getOrNull(col)
}