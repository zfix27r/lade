package app.lade.calendar.internal.appbar.data

import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

internal fun appBarTitleText(
    date: LocalDate,
    today: LocalDate,
    locale: Locale,
): String {
    val ym = YearMonth.from(date)
    val monthFmt = DateTimeFormatter.ofPattern("LLL", locale)
    val monthText = ym.atDay(1).format(monthFmt)
    return if (date.year != today.year) "$monthText ${date.year}" else monthText
}