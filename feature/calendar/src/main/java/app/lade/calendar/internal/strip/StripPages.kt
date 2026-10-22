package app.lade.calendar.internal.strip

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import app.lade.calendar.api.config.StripConfig
import app.lade.calendar.internal.strip.data.stripShiftDate
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import java.time.temporal.WeekFields

@Composable
internal fun StripPages(
    stripDate: LocalDate,
    calendarDate: StateFlow<LocalDate>,
    markedDates: StateFlow<Map<LocalDate, CalendarDataMinModel>>,
    progressState: State<Float>,
    offsetXState: State<Float>,
    widthPx: Int,
    weekFields: WeekFields,
    config: StripConfig,
) {
    val width: Float = widthPx.toFloat()
    Box(modifier = Modifier.fillMaxSize()) {
        for (i in -config.pageRange..config.pageRange) {
            val pageDate = stripShiftDate(
                date = stripDate,
                steps = i,
                isMonthMode = true,
            )
            StripPageSlot(
                pageDate = pageDate,
                stripDate = stripDate,
                calendarDate = calendarDate,
                markedDates = markedDates,
                progressState = progressState,
                offsetXState = offsetXState,
                pageIndex = i,
                pageWidth = width,
                weekFields = weekFields,
                config = config,
            )
        }
    }
}