package app.lade.calendar.internal.list.strip

import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.WeekFields

internal fun stripPageDate(
    anchor: LocalDate,
    isMonthMode: Boolean,
    offset: Int,
): LocalDate = if (isMonthMode) {
    val ym = YearMonth.from(anchor).plusMonths(offset.toLong())
    ym.atDay(anchor.dayOfMonth.coerceAtMost(ym.lengthOfMonth()))
} else {
    anchor.plusWeeks(offset.toLong())
}

internal fun weeksOfMonth(date: LocalDate, weekFields: WeekFields): List<List<LocalDate>> {
    val month = YearMonth.from(date)
    val firstOfMonth = month.atDay(1)
    val firstWeekStart = firstOfMonth.with(weekFields.dayOfWeek(), 1L)

    val weeks = mutableListOf<List<LocalDate>>()
    var cursor = firstWeekStart
    repeat(6) {
        weeks += (0L..6L).map { cursor.plusDays(it) }
        cursor = cursor.plusWeeks(1)
    }
    return weeks
}