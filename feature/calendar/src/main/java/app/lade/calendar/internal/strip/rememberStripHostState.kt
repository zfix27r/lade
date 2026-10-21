package app.lade.calendar.internal.strip

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import app.lade.calendar.api.config.StripConfig
import app.lade.calendar.internal.strip.data.StripStateHolder
import app.lade.calendar.internal.strip.data.stripWeeks
import app.lade.calendar.internal.strip.layout.StripLayout
import app.lade.calendar.internal.strip.swipe.StripSwipeGesture
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import java.time.LocalDate
import java.time.temporal.WeekFields

@Composable
internal fun rememberStripHostState(
    currentDate: StateFlow<LocalDate>,
    config: StripConfig,
    listState: LazyListState,
    weekFields: WeekFields,
    fullScrollPx: Float,
): StripHostState {
    val holder = remember { StripStateHolder(initialDate = currentDate.value) }

    LaunchedEffect(holder, currentDate) {
        snapshotFlow { currentDate.value }
            .distinctUntilChanged()
            .collect { holder.followCalendarDate(it) }
    }

    val gesture = remember(holder, config) { StripSwipeGesture(holder, config) }

    val connection = remember(holder, gesture, listState, fullScrollPx) {
        StripConnection(holder, gesture, listState, fullScrollPx)
    }

    val layout = remember {
        derivedStateOf {
            val stripDate = holder.stripDate.value
            val weeks = stripWeeks(stripDate, weekFields)
            val activeIndex = weeks.indexOfFirst { it.contains(stripDate) }.coerceAtLeast(0)
            StripLayout(
                activeWeekIndex = activeIndex,
                totalRows = weeks.size,
                rowHeight = config.rowHeight,
            )
        }
    }

    return remember(holder, gesture, connection) {
        StripHostState(
            holder = holder,
            gesture = gesture,
            connection = connection,
            swipeState = holder.swipeState,
            stripDate = holder.stripDate,
            dateSelected = holder.dateSelected,
            layout = layout,
            progress = holder.targetProgress,
        )
    }
}