package app.lade.calendar.internal.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLocale
import app.lade.calendar.api.config.CalendarConfig
import app.lade.calendar.api.config.DefaultCalendarConfig
import app.lade.calendar.internal.domain.CalendarStateModel
import app.lade.calendar.internal.list.strip.StripConnection
import app.lade.calendar.internal.list.strip.StripSwipeRow
import app.lade.calendar.internal.list.strip.data.StripState
import app.lade.calendar.internal.list.strip.data.StripStateHolder
import app.lade.calendar.internal.list.strip.data.stripWeeks
import app.lade.calendar.internal.list.strip.layout.stripLayoutRemember
import app.lade.calendar.internal.list.strip.rememberStripController
import app.lade.calendardata.api.CalendarCardModel
import java.time.LocalDate
import java.time.temporal.WeekFields

@Composable
internal fun ListLayout(
    state: CalendarStateModel,
    stripState: StripState,
    strip: StripStateHolder,
    offsetXState: State<Float>,
    onDateSelected: (LocalDate) -> Unit,
    onOpenAgenda: (CalendarCardModel) -> Unit,
    onEntryLongPress: (CalendarCardModel) -> Unit,
    onToggleDone: (entryId: Long, date: LocalDate) -> Unit,
    onGoalToggle: (Long, LocalDate, Long) -> Unit,
    modifier: Modifier = Modifier,
    config: CalendarConfig = DefaultCalendarConfig,
) {
    val listState = rememberLazyListState()
    val density = LocalDensity.current
    val locale = LocalLocale.current.platformLocale
    val weekFields = remember(locale) { WeekFields.of(locale) }

    val stripController = rememberStripController(strip, config.strip)

    val fullScrollPx = with(density) {
        (config.strip.rowHeight * config.strip.scrollRows).toPx()
    }

    val connection = StripConnection(
        gesture = stripController.gesture,
        listState = listState,
        fullScrollPx = fullScrollPx,
    )

    val centralWeeks = remember(stripState.date, weekFields) {
        stripWeeks(stripState.date, weekFields)
    }
    val layout = stripLayoutRemember(
        weeks = centralWeeks,
        activeDate = state.currentDate,
        config = config.strip,
    )
    val panelHeight = config.list.bottomPaddingForInputBar

    val progressState: State<Float> = strip.state
        .collectAsState(initial = stripState)
        .let { stateFlowState ->
            remember(stateFlowState) {
                derivedStateOf { stateFlowState.value.progress }
            }
        }

    val topOffsetProvider: () -> androidx.compose.ui.unit.Dp = remember(layout, progressState) {
        { layout.listTopOffsetY(progressState.value) }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(connection),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter),
            ) {
                StripSwipeRow(
                    currentDate = state.currentDate,
                    stripState = stripState,
                    progressState = progressState,
                    offsetXState = offsetXState,
                    controller = stripController,
                    markedDates = state.markedDates,
                    onDateSelected = onDateSelected,
                    config = config.strip,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            ListContent(
                entries = state.entries,
                topOffsetProvider = topOffsetProvider,
                config = config.list,
                panelHeight = panelHeight,
                listState = listState,
                onOpenAgenda = onOpenAgenda,
                onEntryLongPress = onEntryLongPress,
                onToggleDone = onToggleDone,
                onGoalToggle = onGoalToggle,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}