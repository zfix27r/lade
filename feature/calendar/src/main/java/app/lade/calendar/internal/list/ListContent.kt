package app.lade.calendar.internal.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.lade.calendar.api.config.ListConfig
import app.lade.calendar.internal.card.CalendarCardEntry
import app.lade.calendar.internal.card.expandable.CardExpandableKey
import app.lade.calendar.internal.card.expandable.rememberCardExpandableState
import app.lade.calendardata.api.agenda.CalendarDataAgendaModel
import app.lade.calendardata.api.agenda.CalendarDataDayModel
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate

@Composable
internal fun ListContent(
    days: StateFlow<List<CalendarDataDayModel>>,
    topOffsetProvider: () -> Dp,
    config: ListConfig,
    panelHeight: Dp,
    listState: LazyListState,
    onOpenAgenda: (LocalDate, CalendarDataAgendaModel) -> Unit,
    onEntryLongPress: (LocalDate, CalendarDataAgendaModel) -> Unit,
    onGoalToggle: (Long, LocalDate, Long) -> Unit,
    onGoalValueChange: (Long, LocalDate, Long, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val daysValue by days.collectAsStateWithLifecycle()
    val expandableState = rememberCardExpandableState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .offset { IntOffset(0, topOffsetProvider().roundToPx()) }
            .background(MaterialTheme.colorScheme.surface),
    ) {
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(
                start = config.contentPadding,
                end = config.contentPadding,
                bottom = panelHeight + config.contentBottomPadding,
            ),
            verticalArrangement = Arrangement.spacedBy(config.entrySpacing),
            modifier = Modifier.fillMaxSize(),
        ) {
            daysValue.forEach { day ->
                items(
                    items = day.agendas,
                    key = { agenda -> "entry-${agenda.entry.id}-${day.date.toEpochDay()}" },
                ) { agenda ->
                    val expandableKey = CardExpandableKey.of(
                        entryId = agenda.entry.id,
                        epochDay = day.date.toEpochDay(),
                    )
                    val isExpanded = expandableState.isExpanded(expandableKey)

                    CalendarCardEntry(
                        agenda = agenda,
                        date = day.date,
                        expanded = isExpanded,
                        onOpenAgenda = { onOpenAgenda(day.date, agenda) },
                        onLongPress = { onEntryLongPress(day.date, agenda) },
                        onToggleExpand = { expandableState.toggle(expandableKey) },
                        onGoalBinaryClick = { goalId ->
                            onGoalToggle(agenda.entry.id, day.date, goalId)
                        },
                        onGoalValueChange = { goalId, value ->
                            onGoalValueChange(agenda.entry.id, day.date, goalId, value)
                        },
                    )
                }
            }
        }
        if (daysValue.all { it.agendas.isEmpty() }) {
            CalendarListEmptyState(
                icon = Icons.AutoMirrored.Filled.EventNote,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}