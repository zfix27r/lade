package app.lade.calendar.internal.dates

import java.time.LocalDate

internal fun isCalendarDatesSelected(
    date: LocalDate,
    selectedDate: LocalDate,
): Boolean = date == selectedDate