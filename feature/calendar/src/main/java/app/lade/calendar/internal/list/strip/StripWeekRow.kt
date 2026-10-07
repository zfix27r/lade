package app.lade.calendar.internal.list.strip

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import app.lade.calendar.api.config.StripConfig
import app.lade.calendar.internal.list.strip.anim.outOfMonthAlpha
import app.lade.calendardata.api.DayProgress
import app.lade.ui.theme.LocalToday
import java.time.LocalDate
import java.time.Month

@Composable
internal fun StripWeekRow(
    week: List<LocalDate>,
    currentDate: LocalDate,
    pageMonth: Month,
    markedDates: Map<LocalDate, DayProgress>,
    progressState: State<Float>,
    config: StripConfig,
    modifier: Modifier = Modifier,
) {
    val today = LocalToday.current

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
                            outOfMonthAlpha(
                                progress = progressState.value,
                                start = config.outOfMonthAlphaStart,
                            )
                        } else {
                            1f
                        }
                    },
                contentAlignment = Alignment.Center,
            ) {
                StripMonthDayCircle(
                    date = date,
                    isActive = date == currentDate,
                    isToday = date == today,
                    progress = markedDates[date],
                    isOutOfMonth = outOfMonth,
                    config = config,
                )
            }
        }
    }
}