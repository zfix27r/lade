package app.lade.calendar.internal.dates

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate

@Composable
internal fun CalendarDatesView(
    weeks: List<CalendarDatesWeekModel>,
    visibleDate: StateFlow<LocalDate>,
    selectedDate: StateFlow<LocalDate>,
    onDateClick: (LocalDate) -> Unit,
    onVisibleDateChange: (LocalDate) -> Unit,
    style: CalendarDatesStyle,
    modifier: Modifier = Modifier,
    externalListState: LazyListState? = null,
) {
    if (style.visibleWeeks <= 1) {
        CalendarDatesRowView(
            weeks = weeks,
            visibleDate = visibleDate,
            selectedDate = selectedDate,
            onDateClick = onDateClick,
            onVisibleDateChange = onVisibleDateChange,
            style = style,
            modifier = modifier,
            externalListState = externalListState,
        )
    } else {
        CalendarDatesGridView(
            weeks = weeks,
            visibleDate = visibleDate,
            selectedDate = selectedDate,
            onDateClick = onDateClick,
            onVisibleDateChange = onVisibleDateChange,
            style = style,
            modifier = modifier,
        )
    }
}