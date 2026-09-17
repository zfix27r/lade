package app.lade.calendar.ui.component.list.collapse

import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.Locale

fun weeksOfMonth(date: LocalDate, locale: Locale): List<List<LocalDate?>> {
    val month = YearMonth.from(date)
    val firstDayOfWeek = WeekFields.of(locale).firstDayOfWeek
    val leadingEmptyCells = (month.atDay(1).dayOfWeek.value - firstDayOfWeek.value + 7) % 7
    val cells = buildList {
        repeat(leadingEmptyCells) { add(null) }
        for (day in 1..month.lengthOfMonth()) add(month.atDay(day))
        while (size % 7 != 0) add(null)
    }
    return cells.chunked(7).filter { week -> week.any { it != null } }
}