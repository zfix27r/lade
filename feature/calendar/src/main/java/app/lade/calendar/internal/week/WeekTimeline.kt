package app.lade.calendar.internal.week

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import app.lade.calendar.api.config.WeekConfig
import app.lade.calendar.internal.dates.CalendarDatesWeekModel
import app.lade.calendar.internal.week.data.GRID_HOUR_FROM
import app.lade.calendar.internal.week.data.GRID_HOUR_TO
import app.lade.calendar.internal.week.data.coversHour
import app.lade.calendardata.api.agenda.CalendarDataAgendaModel
import app.lade.entry.EntryKind
import java.time.LocalDate

@Composable
internal fun WeekTimeline(
    week: CalendarDatesWeekModel,
    agendasByDate: Map<LocalDate, List<CalendarDataAgendaModel>>,
    today: LocalDate,
    selectedDate: LocalDate,
    onOpenDay: (LocalDate) -> Unit,
    config: WeekConfig,
    modifier: Modifier = Modifier,
) {
    val hours = GRID_HOUR_FROM until GRID_HOUR_TO

    Row(modifier = modifier) {
        week.days.forEach { day ->
            Column(modifier = Modifier.weight(1f)) {
                hours.forEach { hour ->
                    val covering = agendasByDate[day.date].orEmpty().filter { agenda ->
                        val kind = agenda.entry.kind
                        if (kind != EntryKind.SCHEDULE && kind != EntryKind.EVENT) {
                            return@filter false
                        }
                        val start = agenda.entry.timeFrom ?: return@filter false
                        val end = agenda.entry.timeTo ?: return@filter false
                        coversHour(start, end, hour)
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(config.hourHeight)
                            .border(
                                width = config.gridCellBorderWidth,
                                color = MaterialTheme.colorScheme.outlineVariant,
                            )
                            .clickable { onOpenDay(day.date) }
                            .padding(config.gridCellPadding),
                    ) {
                        if (covering.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = covering.first().entry.title.ifBlank { "·" },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}