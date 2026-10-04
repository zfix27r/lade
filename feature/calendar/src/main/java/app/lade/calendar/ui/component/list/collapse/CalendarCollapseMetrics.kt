package app.lade.calendar.ui.component.list.collapse

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.unit.Dp
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.WeekFields

@Immutable
data class CalendarCollapseMetrics(
    val weeks: List<List<LocalDate>>,
    val activeWeekIndex: Int,
    val rowHeight: Dp,
    val monthHeight: Dp,
    val collapsedHeight: Dp,
) {
    fun stripHeight(): Dp = monthHeight

    fun columnOffsetY(progress: Float): Dp =
        rowHeight * activeWeekIndex * (progress - 1f)

    fun listTopOffset(progress: Float): Dp =
        collapsedHeight + (monthHeight - collapsedHeight) * progress
}

@Composable
fun rememberCalendarCollapseMetrics(
    currentDate: LocalDate,
    config: CalendarCollapseConfig,
): CalendarCollapseMetrics {
    val locale = LocalLocale.current.platformLocale
    val weekFields = remember(locale) { WeekFields.of(locale) }
    val weeks = remember(currentDate, locale) { weeksOfMonth(currentDate, weekFields) }
    val activeWeekIndex = remember(weeks, currentDate) {
        weeks.indexOfFirst { week -> week.contains(currentDate) }.coerceAtLeast(0)
    }
    return remember(weeks, activeWeekIndex, config) {
        CalendarCollapseMetrics(
            weeks = weeks,
            activeWeekIndex = activeWeekIndex,
            rowHeight = config.rowHeight,
            monthHeight = config.rowHeight * 6,
            collapsedHeight = config.rowHeight,
        )
    }
}

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