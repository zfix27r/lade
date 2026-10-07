package app.lade.calendar.internal.week

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLocale
import app.lade.calendar.api.config.CalendarConfig
import app.lade.calendar.api.config.DefaultCalendarConfig
import app.lade.calendar.internal.domain.CalendarStateModel
import app.lade.calendar.internal.week.data.weekDays
import java.time.LocalDate
import java.time.temporal.WeekFields

@Composable
internal fun WeekLayout(
    state: CalendarStateModel,
    onDateSelected: (LocalDate) -> Unit,
    onEditEntry: (Long) -> Unit,
    modifier: Modifier = Modifier,
    config: CalendarConfig = DefaultCalendarConfig,
) {
    val weekFields = WeekFields.of(LocalLocale.current.platformLocale)
    val days = weekDays(state.currentDate, weekFields)
    val entriesByDate = state.entries.groupBy { it.date }
    val today = LocalDate.now()

    Column(modifier = modifier.fillMaxSize()) {
        WeekStrip(
            currentDate = state.currentDate,
            entries = state.entries,
            onDateSelected = onDateSelected,
            config = config.week,
        )
        WeekTimeline(
            days = days,
            entriesByDate = entriesByDate,
            today = today,
            selectedDate = state.currentDate,
            onOpenDay = onDateSelected,
            config = config.week,
        )
    }
}