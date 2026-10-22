package app.lade.calendar.internal.strip

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import app.lade.calendar.api.config.StripConfig
import app.lade.calendar.internal.strip.layout.StripLayout
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import kotlin.math.roundToInt

@Composable
internal fun StripPage(
    pageDate: LocalDate,
    calendarDate: StateFlow<LocalDate>,
    markedDates: StateFlow<Map<LocalDate, CalendarDataMinModel>>,
    progressState: State<Float>,
    offsetXState: State<Float>,
    pageIndex: Int,
    pageWidth: Float,
    weeks: List<List<LocalDate>>,
    layout: StripLayout,
    config: StripConfig,
    modifier: Modifier = Modifier,
) {
    val pageMonth = pageDate.month

    Column(
        modifier = modifier
            .fillMaxWidth()
            .offset {
                IntOffset(
                    x = (offsetXState.value + pageIndex * pageWidth).roundToInt(),
                    y = layout.rowColumnOffsetY(progressState.value).roundToPx(),
                )
            },
    ) {
        weeks.forEach { week ->
            StripWeekRow(
                week = week,
                calendarDate = calendarDate,
                pageMonth = pageMonth,
                markedDates = markedDates,
                progressState = progressState,
                config = config,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(layout.rowHeight),
            )
        }
    }
}