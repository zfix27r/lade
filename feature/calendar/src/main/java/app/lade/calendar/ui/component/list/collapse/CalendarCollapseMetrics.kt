package app.lade.calendar.ui.component.list.collapse

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.unit.Dp
import java.time.LocalDate

@Immutable
data class CalendarCollapseMetrics(
    val weeks: List<List<LocalDate?>>,
    val currentWeekIndex: Int,
    val rowHeight: Dp,
    val monthHeight: Dp,
    val collapsedHeight: Dp,
    val fullScrollPx: Float,
) {
    fun stripHeight(): Dp = monthHeight

    fun listTopOffset(progress: Float): Dp =
        collapsedHeight + (monthHeight - collapsedHeight) * progress

    fun columnOffsetY(progress: Float): Dp =
        -rowHeight * currentWeekIndex * (1f - progress)
}

@Composable
fun rememberCalendarCollapseMetrics(
    currentDate: LocalDate,
    config: CalendarCollapseConfig,
    fullScrollPx: Float,
): CalendarCollapseMetrics {
    val locale = LocalLocale.current.platformLocale
    val weeks = remember(currentDate, locale) { weeksOfMonth(currentDate, locale) }
    val currentWeekIndex = remember(weeks, currentDate) {
        weeks.indexOfFirst { week -> week.any { it == currentDate } }.coerceAtLeast(0)
    }
    return remember(weeks, currentWeekIndex, config, fullScrollPx) {
        CalendarCollapseMetrics(
            weeks = weeks,
            currentWeekIndex = currentWeekIndex,
            rowHeight = config.rowHeight,
            monthHeight = config.rowHeight * weeks.size,
            collapsedHeight = config.rowHeight,
            fullScrollPx = fullScrollPx,
        )
    }
}