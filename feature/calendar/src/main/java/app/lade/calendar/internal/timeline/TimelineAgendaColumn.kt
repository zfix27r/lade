package app.lade.calendar.internal.timeline

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.lade.calendar.api.config.CalendarConfig
import app.lade.calendar.api.config.DefaultCalendarConfig
import app.lade.calendar.internal.card.CalendarCardEntry
import app.lade.calendar.internal.card.expandable.CardExpandableKey
import app.lade.calendar.internal.card.expandable.rememberCardExpandableState
import app.lade.calendardata.api.agenda.CalendarDataAgendaModel
import app.lade.calendardata.api.agenda.CalendarDataDayModel
import java.time.LocalDate

@Composable
internal fun TimelineAgendaColumn(
    day: CalendarDataDayModel,
    onOpenAgenda: (LocalDate, CalendarDataAgendaModel) -> Unit,
    onGoalToggle: (Long, LocalDate, Long) -> Unit,
    onGoalValueChange: (Long, LocalDate, Long, Int) -> Unit,
    onEntryLongPress: (LocalDate, CalendarDataAgendaModel) -> Unit,
    modifier: Modifier = Modifier,
    config: CalendarConfig = DefaultCalendarConfig,
) {
    val layout = config.timelineLayout
    val expandableState = rememberCardExpandableState()

    if (day.agendas.isEmpty() && !layout.showEmptyDays) return

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(layout.daySpacing),
    ) {
        if (day.agendas.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(layout.emptyDayHeight),
            )
        } else {
            day.agendas.forEach { agenda ->
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
}