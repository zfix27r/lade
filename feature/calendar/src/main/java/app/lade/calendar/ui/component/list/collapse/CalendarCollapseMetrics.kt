package app.lade.calendar.ui.component.list.collapse

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.unit.Dp
import app.lade.calendar.domain.CalendarListStripMode
import java.time.LocalDate
import java.time.temporal.WeekFields

@Immutable
data class CalendarCollapseMetrics(
    val weeks: List<List<LocalDate?>>,
    val activeWeekIndex: Int,
    val rowHeight: Dp,
    val monthHeight: Dp,
    val collapsedHeight: Dp,
    val fullScrollPx: Float,
) {
    fun stripHeight(): Dp = monthHeight

    fun listTopOffset(progress: Float): Dp =
        collapsedHeight + (monthHeight - collapsedHeight) * progress

    fun columnOffsetY(progress: Float): Dp =
        -rowHeight * activeWeekIndex * (1f - progress)
}

@Composable
fun rememberCalendarCollapseMetrics(
    pageDate: LocalDate,
    anchorDate: LocalDate,
    stripMode: CalendarListStripMode,
    config: CalendarCollapseConfig,
    fullScrollPx: Float,
): CalendarCollapseMetrics {
    val locale = LocalLocale.current.platformLocale
    val weekFields = WeekFields.of(locale)
    val weeks = remember(pageDate, stripMode, locale) {
        when (stripMode) {
            CalendarListStripMode.WEEK -> {
                val weekStart = pageDate.with(weekFields.dayOfWeek(), 1L)
                listOf((0L..6L).map { weekStart.plusDays(it) })
            }
            CalendarListStripMode.MONTH -> weeksOfMonth(pageDate, locale)
        }
    }
    val activeWeekIndex = remember(weeks, anchorDate) {
        weeks.indexOfFirst { week -> week.any { it == anchorDate } }.coerceAtLeast(0)
    }
    return remember(weeks, activeWeekIndex, config, fullScrollPx) {
        CalendarCollapseMetrics(
            weeks = weeks,
            activeWeekIndex = activeWeekIndex,
            rowHeight = config.rowHeight,
            monthHeight = config.rowHeight * weeks.size,
            collapsedHeight = config.rowHeight,
            fullScrollPx = fullScrollPx,
        )
    }
}