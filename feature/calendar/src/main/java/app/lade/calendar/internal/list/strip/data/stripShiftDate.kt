package app.lade.calendar.internal.list.strip.data

import java.time.LocalDate

internal fun stripShiftDate(
    date: LocalDate,
    steps: Int,
    isMonthMode: Boolean,
): LocalDate = if (isMonthMode) {
    date.plusMonths(steps.toLong())
} else {
    date.plusWeeks(steps.toLong())
}