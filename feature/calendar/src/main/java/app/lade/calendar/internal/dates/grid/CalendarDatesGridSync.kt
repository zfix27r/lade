package app.lade.calendar.internal.dates.grid

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import app.lade.calendar.internal.dates.CalendarDatesWeekModel
import kotlinx.coroutines.flow.distinctUntilChanged
import java.time.LocalDate

@Composable
internal fun CalendarDatesGridSync(
    listState: LazyListState,
    weeks: List<CalendarDatesWeekModel>,
    enabled: Boolean,
    onVisibleDateChange: (LocalDate) -> Unit,
) {
    LaunchedEffect(listState, weeks, enabled) {
        if (!enabled) return@LaunchedEffect
        snapshotFlow { listState.firstVisibleItemIndex to listState.isScrollInProgress }
            .distinctUntilChanged()
            .collect { (index, scrolling) ->
                if (scrolling) return@collect
                weeks.getOrNull(index)?.let { week ->
                    onVisibleDateChange(week.days.first().date)
                }
            }
    }
}