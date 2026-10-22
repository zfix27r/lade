package app.lade.calendar.internal.strip

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import app.lade.calendar.api.config.StripConfig
import app.lade.calendar.internal.strip.data.stripWeeks
import app.lade.calendar.internal.strip.layout.StripLayout
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import java.time.temporal.WeekFields

@Composable
internal fun StripPageSlot(
    pageDate: LocalDate,
    stripDate: LocalDate,
    calendarDate: StateFlow<LocalDate>,
    markedDates: StateFlow<Map<LocalDate, CalendarDataMinModel>>,
    progressState: State<Float>,
    offsetXState: State<Float>,
    pageIndex: Int,
    pageWidth: Float,
    weekFields: WeekFields,
    config: StripConfig,
) {
    val pageWeeks = remember(pageDate, weekFields) {
        stripWeeks(pageDate, weekFields)
    }
    val pageLayout: StripLayout = remember(pageWeeks, stripDate, config) {
        val idx = pageWeeks.indexOfFirst { it.contains(stripDate) }.coerceAtLeast(0)
        StripLayout(
            activeWeekIndex = idx,
            totalRows = pageWeeks.size,
            rowHeight = config.rowHeight,
        )
    }

    StripPage(
        pageDate = pageDate,
        calendarDate = calendarDate,
        markedDates = markedDates,
        progressState = progressState,
        offsetXState = offsetXState,
        pageIndex = pageIndex,
        pageWidth = pageWidth,
        weeks = pageWeeks,
        layout = pageLayout,
        config = config,
    )
}