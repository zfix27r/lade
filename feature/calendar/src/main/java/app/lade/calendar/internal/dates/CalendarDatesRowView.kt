package app.lade.calendar.internal.dates

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLocale
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.lade.calendar.internal.dates.grid.CalendarDatesGridSync
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import java.time.temporal.WeekFields

@Composable
internal fun CalendarDatesRowView(
    weeks: List<CalendarDatesWeekModel>,
    visibleDate: StateFlow<LocalDate>,
    selectedDate: StateFlow<LocalDate>,
    onDateClick: (LocalDate) -> Unit,
    onVisibleDateChange: (LocalDate) -> Unit,
    style: CalendarDatesStyle,
    modifier: Modifier = Modifier,
    externalListState: LazyListState? = null,
) {
    val locale = LocalLocale.current.platformLocale
    val weekFields = remember(locale) { WeekFields.of(locale) }
    val weekdays = remember(weekFields) {
        val first = weekFields.firstDayOfWeek
        (0..6).map { first.plus(it.toLong()) }
    }
    val visible by visibleDate.collectAsStateWithLifecycle()
    val selected by selectedDate.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        CalendarDatesRow(
            weekdays = weekdays,
            style = style,
        )
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            val density = LocalDensity.current
            val cellHeight = maxHeight
            val cellWidthPx = with(density) { (maxWidth.toPx() / 7f).toInt() }
            val cellWidth = with(density) { cellWidthPx.toFloat().toDp() }

            val listState = externalListState ?: rememberLazyListState()

            var isFirstRender by remember { mutableStateOf(true) }
            var isProgrammaticScroll by remember { mutableStateOf(false) }

            LaunchedEffect(visible, weeks.size) {
                if (isFirstRender) {
                    isFirstRender = false
                    return@LaunchedEffect
                }
                val targetIndex = weeks.indexOfFirst { week ->
                    week.days.any { it.date == visible }
                }
                if (targetIndex < 0) return@LaunchedEffect
                if (listState.firstVisibleItemIndex == targetIndex) return@LaunchedEffect

                isProgrammaticScroll = true
                try {
                    listState.animateScrollToItem(targetIndex)
                } finally {
                    isProgrammaticScroll = false
                }
            }

            CalendarDatesGridSync(
                listState = listState,
                weeks = weeks,
                enabled = !isProgrammaticScroll,
                onVisibleDateChange = onVisibleDateChange,
            )

            LazyRow(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(cellHeight),
            ) {
                items(
                    items = weeks,
                    key = { week -> week.key.asString },
                ) { week ->
                    Row(
                        modifier = Modifier
                            .width(cellWidth * 7)
                            .height(cellHeight),
                    ) {
                        week.days.forEach { day ->
                            CalendarDatesCell(
                                date = day.date,
                                selectedDate = selected,
                                markTotal = day.mark?.total ?: 0,
                                onClick = onDateClick,
                                style = style,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxSize(),
                            )
                        }
                    }
                }
            }
        }
    }
}