package app.lade.calendar.internal.dates

import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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

/**
 * Как листается сетка недель.
 * - [HORIZONTAL_PAGE] — недели идут слева направо, 1 неделя = страница (стрип недели).
 * - [VERTICAL_PAGE] — недели идут сверху вниз, `visibleWeeks` недель = страница (месяц).
 */
internal enum class CalendarDatesGridLayout { HORIZONTAL_PAGE, VERTICAL_PAGE }

/**
 * Универсальная сетка календаря (пакет `dates`).
 *
 * Один движок для всех режимов «сетки недель»: WEEK (горизонтальный пагер),
 * MONTH (вертикальный пагер), и как основа для YEAR. Потребитель задаёт:
 * - как листается сетка ([layout]);
 * - сколько недель видно ([visibleWeeks]);
 * - что рендерить в строке недели ([weekRow], по умолчанию 7 ячеек дат).
 *
 * Сетка сама: пагинирует со снапом по странице, программно скроллит к [visibleDate],
 * и сообщает об остановке через [onVisibleDateChange].
 */
@Composable
internal fun CalendarDatesGrid(
    weeks: List<CalendarDatesWeekModel>,
    layout: CalendarDatesGridLayout,
    visibleWeeks: Int,
    style: CalendarDatesStyle,
    visibleDate: StateFlow<LocalDate>,
    selectedDate: StateFlow<LocalDate>,
    onDateClick: (LocalDate) -> Unit,
    onVisibleDateChange: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    externalListState: LazyListState? = null,
    userScrollEnabled: Boolean = true,
    showWeekdayHeader: Boolean = true,
    weekRow: @Composable (CalendarDatesWeekModel) -> Unit = { week ->
        val selected by selectedDate.collectAsStateWithLifecycle()
        CalendarDatesWeekRow(
            week = week,
            selectedDate = selected,
            onDateClick = onDateClick,
            style = style,
        )
    },
) {
    val locale = LocalLocale.current.platformLocale
    val weekFields = remember(locale) { WeekFields.of(locale) }
    val weekdays = remember(weekFields) {
        val first = weekFields.firstDayOfWeek
        (0..6).map { first.plus(it.toLong()) }
    }
    val visible by visibleDate.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        if (showWeekdayHeader) {
            CalendarDatesRow(
                weekdays = weekdays,
                style = style,
            )
        }
        if (weeks.isEmpty()) {
            Box(modifier = Modifier.weight(1f))
        } else {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                val density = LocalDensity.current
                when (layout) {
                    CalendarDatesGridLayout.HORIZONTAL_PAGE -> {
                        val cellWidthPx = with(density) { (maxWidth.toPx() / 7f).toInt() }
                        val cellWidth = with(density) { cellWidthPx.toFloat().toDp() }
                        val listState = externalListState ?: rememberLazyListState()

                        CalendarDatesGridScrollSync(
                            listState = listState,
                            weeks = weeks,
                            visible = visible,
                            onVisibleDateChange = onVisibleDateChange,
                        )

                        LazyRow(
                            state = listState,
                            userScrollEnabled = userScrollEnabled,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(maxHeight),
                        ) {
                            items(
                                items = weeks,
                                key = { week -> week.key.asString },
                            ) { week ->
                                Row(
                                    modifier = Modifier
                                        .width(cellWidth * 7)
                                        .fillMaxHeight(),
                                ) {
                                    weekRow(week)
                                }
                            }
                        }
                    }

                    CalendarDatesGridLayout.VERTICAL_PAGE -> {
                        val cellHeightPx = with(density) {
                            (maxHeight.toPx() / visibleWeeks).toInt()
                        }
                        val cellHeight = with(density) { cellHeightPx.toFloat().toDp() }

                        val firstOfMonth = visible.withDayOfMonth(1)
                        val initialIndex = remember(weeks, firstOfMonth) {
                            weeks.indexOfFirst { week ->
                                week.days.any { it.date == firstOfMonth }
                            }.coerceAtLeast(0)
                        }
                        val listState = externalListState ?: rememberLazyListState(
                            initialFirstVisibleItemIndex = initialIndex,
                        )
                        val snapProvider = remember(listState, cellHeightPx) {
                            object : SnapLayoutInfoProvider {
                                override fun calculateSnapOffset(velocity: Float): Float {
                                    val itemHeight = cellHeightPx.toFloat()
                                    val currentOffset = listState.firstVisibleItemScrollOffset.toFloat()
                                    return if (velocity > 0f) {
                                        itemHeight - currentOffset
                                    } else {
                                        -currentOffset
                                    }
                                }
                            }
                        }
                        val fling = rememberSnapFlingBehavior(snapLayoutInfoProvider = snapProvider)

                        CalendarDatesGridScrollSync(
                            listState = listState,
                            weeks = weeks,
                            visible = visible,
                            onVisibleDateChange = onVisibleDateChange,
                        )

                        LazyColumn(
                            state = listState,
                            flingBehavior = fling,
                            userScrollEnabled = userScrollEnabled,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(cellHeight * visibleWeeks),
                        ) {
                            items(
                                items = weeks,
                                key = { week -> week.key.asString },
                            ) { week ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(cellHeight),
                                ) {
                                    weekRow(week)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Общая синхронизация сетки: программный скролл к [visible] + эмиссия
 * [onVisibleDateChange] при остановке скролла (см. [CalendarDatesGridSync]).
 */
@Composable
private fun CalendarDatesGridScrollSync(
    listState: LazyListState,
    weeks: List<CalendarDatesWeekModel>,
    visible: LocalDate,
    onVisibleDateChange: (LocalDate) -> Unit,
) {
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
}
