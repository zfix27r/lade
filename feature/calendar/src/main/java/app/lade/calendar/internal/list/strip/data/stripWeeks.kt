package app.lade.calendar.internal.list.strip.data

import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.WeekFields

internal fun stripWeeks(
    date: LocalDate,
    weekFields: WeekFields,
): List<List<LocalDate>> = weeksOfMonth(date, weekFields)

private fun weeksOfMonth(date: LocalDate, weekFields: WeekFields): List<List<LocalDate>> {
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