package app.lade.calendar.internal.dates

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.lade.calendar.api.config.CalendarDatesConfig
import app.lade.calendar.api.config.DefaultCalendarDatesConfig
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate

@Composable
internal fun CalendarDatesHost(
    weeks: StateFlow<List<CalendarDatesWeekModel>>,
    visibleDate: StateFlow<LocalDate>,
    selectedDate: StateFlow<LocalDate>,
    onDateClick: (LocalDate) -> Unit,
    onVisibleDateChange: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    config: CalendarDatesConfig = DefaultCalendarDatesConfig,
    listState: LazyListState? = null,
) {
    val weeksValue by weeks.collectAsStateWithLifecycle()
    val style = buildCalendarDatesStyle(
        compact = config.compact,
        visibleWeeks = config.visibleWeeks,
    )

    CalendarDatesGrid(
        weeks = weeksValue,
        layout = CalendarDatesGridLayout.VERTICAL_PAGE,
        visibleWeeks = config.visibleWeeks,
        style = style,
        visibleDate = visibleDate,
        selectedDate = selectedDate,
        onDateClick = onDateClick,
        onVisibleDateChange = onVisibleDateChange,
        modifier = modifier,
        externalListState = listState,
    )
}
