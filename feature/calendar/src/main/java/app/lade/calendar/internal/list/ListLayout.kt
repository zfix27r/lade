package app.lade.calendar.internal.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Dp
import app.lade.calendar.api.config.CalendarConfig
import app.lade.calendar.api.config.DefaultCalendarConfig
import app.lade.calendar.internal.data.MarkedDatesStore
import app.lade.calendar.internal.domain.CalendarStateModel
import app.lade.calendar.internal.list.strip.StripHost
import app.lade.calendar.internal.list.strip.StripHostParams
import app.lade.calendardata.api.CalendarCardModel
import java.time.LocalDate

@Composable
internal fun ListLayout(
    state: CalendarStateModel,
    markedDatesStore: MarkedDatesStore,
    onDateSelected: (LocalDate) -> Unit,
    onOpenAgenda: (CalendarCardModel) -> Unit,
    onEntryLongPress: (CalendarCardModel) -> Unit,
    onGoalToggle: (Long, LocalDate, Long) -> Unit,
    onGoalValueChange: (Long, LocalDate, Long, Int) -> Unit,
    onLoadGoalDetails: (Long, LocalDate) -> Unit,
    onStartTimer: (Long, LocalDate) -> Unit,
    onFinishTimer: (Long, LocalDate, Int) -> Unit,
    modifier: Modifier = Modifier,
    config: CalendarConfig = DefaultCalendarConfig,
) {
    val listState = rememberLazyListState()
    val markedDates = markedDatesStore.marked.collectAsState()
    val panelHeight = config.list.bottomPaddingForInputBar

    Box(modifier = modifier.fillMaxSize()) {
        val stripResult = StripHost(
            StripHostParams(
                calendarDate = state.currentDate,
                calendarMarkedDates = markedDates,
                config = config.strip,
                listState = listState,
                onDateSelected = onDateSelected,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter),
            )
        )

        val topOffsetProvider: () -> Dp = remember(stripResult) {
            stripResult.topOffset
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(stripResult.listPort.connection),
        ) {
            ListContent(
                entries = state.entries,
                topOffsetProvider = topOffsetProvider,
                config = config.list,
                timerConfig = config.timer,
                panelHeight = panelHeight,
                listState = listState,
                goalDetailsCache = state.goalDetailsCache,
                onOpenAgenda = onOpenAgenda,
                onEntryLongPress = onEntryLongPress,
                onGoalToggle = onGoalToggle,
                onGoalValueChange = onGoalValueChange,
                onLoadGoalDetails = onLoadGoalDetails,
                onStartTimer = onStartTimer,
                onFinishTimer = onFinishTimer,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}