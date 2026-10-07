package app.lade.calendar.internal.week

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import app.lade.calendar.api.config.WeekConfig
import app.lade.calendar.internal.week.data.GRID_HOUR_FROM
import app.lade.calendar.internal.week.data.GRID_HOUR_TO
import app.lade.calendar.internal.week.data.coversHour
import app.lade.calendardata.api.CalendarCardModel
import app.lade.entry.EntryKind
import app.lade.ui.theme.Spacing
import java.time.LocalDate
import java.time.format.TextStyle

@Composable
internal fun WeekTimeline(
    days: List<LocalDate>,
    entriesByDate: Map<LocalDate, List<CalendarCardModel>>,
    today: LocalDate,
    selectedDate: LocalDate,
    onOpenDay: (LocalDate) -> Unit,
    config: WeekConfig,
    modifier: Modifier = Modifier,
) {
    val hours = GRID_HOUR_FROM until GRID_HOUR_TO

    Column(
        modifier = modifier
            .fillMaxSize()
            .horizontalScroll(rememberScrollState())
            .verticalScroll(rememberScrollState())
            .padding(horizontal = config.gridPadding),
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            Box(modifier = Modifier.width(config.hourLabelWidth))
            days.forEach { date ->
                val weekday = date.dayOfWeek.getDisplayName(
                    TextStyle.NARROW,
                    LocalLocale.current.platformLocale
                )
                Text(
                    text = "$weekday\n${date.dayOfMonth}",
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    color = when (date) {
                        selectedDate -> MaterialTheme.colorScheme.primary
                        today -> MaterialTheme.colorScheme.tertiary
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier
                        .width(config.dayColumnWidth)
                        .clickable { onOpenDay(date) }
                        .padding(bottom = Spacing.xs),
                )
            }
        }
        hours.forEach { hour ->
            Row(
                modifier = Modifier.height(config.hourHeight),
                verticalAlignment = Alignment.Top,
            ) {
                Text(
                    text = "%02d".format(hour),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .width(config.hourLabelWidth)
                        .padding(end = Spacing.xs),
                )
                days.forEach { date ->
                    val covering = entriesByDate[date].orEmpty().filter { card ->
                        if (card.entryKind != EntryKind.SCHEDULE && card.entryKind != EntryKind.EVENT) {
                            return@filter false
                        }
                        val start = card.timeFrom ?: return@filter false
                        val end = card.timeTo ?: return@filter false
                        coversHour(start, end, hour)
                    }
                    Box(
                        modifier = Modifier
                            .width(config.dayColumnWidth)
                            .fillMaxHeight()
                            .border(
                                width = config.gridCellBorderWidth,
                                color = MaterialTheme.colorScheme.outlineVariant,
                            )
                            .clickable { onOpenDay(date) }
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
                                    text = covering.first().title.ifBlank { "·" },
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