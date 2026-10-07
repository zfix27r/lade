package app.lade.calendar.internal.list.strip.data

import java.time.LocalDate
import java.time.YearMonth

internal data class StripState(
    val date: LocalDate,
    val widthPx: Int = 0,
    val offsetX: Float = 0f,
    val progress: Float = 1f,
) {
    val anchorMonth: YearMonth get() = YearMonth.from(date)

    val isMonthMode: Boolean get() = progress >= 0.5f

    val prevDate: LocalDate get() = shift(date, -1)
    val nextDate: LocalDate get() = shift(date, 1)

    fun dateAt(offset: Int): LocalDate = shift(date, offset)

    fun shift(date: LocalDate, steps: Int): LocalDate = if (isMonthMode) {
        date.plusMonths(steps.toLong())
    } else {
        date.plusWeeks(steps.toLong())
    }
}