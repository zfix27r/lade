package app.lade.calendar.internal.strip

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.lade.calendar.api.config.StripConfig
import app.lade.calendar.internal.strip.swipe.stripOutOfMonthAlpha
import app.lade.calendardata.api.mark.CalendarDataMarkModel
import app.lade.ui.theme.LocalToday
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import java.time.Month

@Composable
internal fun StripWeekRow(
    week: List<LocalDate>,
    calendarDate: StateFlow<LocalDate>,
    pageMonth: Month,
    markedDates: StateFlow<Map<LocalDate, CalendarDataMarkModel>>,
    progressState: State<Float>,
    config: StripConfig,
    modifier: Modifier = Modifier,
) {
    val today = LocalToday.current
    val selectedDate by calendarDate.collectAsStateWithLifecycle()
    val marks by markedDates.collectAsStateWithLifecycle()

    Row(
        modifier = modifier.padding(horizontal = config.rowPadding),
    ) {
        week.forEach { date ->
            val outOfMonth = date.month != pageMonth

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .graphicsLayer {
                        alpha = if (outOfMonth) {
                            stripOutOfMonthAlpha(
                                progress = progressState.value,
                                threshold = config.outOfMonthAlphaThreshold,
                            )
                        } else {
                            1f
                        }
                    },
                contentAlignment = Alignment.Center,
            ) {
                StripMonthDayCircle(
                    date = date,
                    isActive = date == selectedDate,
                    isToday = date == today,
                    progress = marks[date],
                    isOutOfMonth = outOfMonth,
                    config = config,
                )
            }
        }
    }
}