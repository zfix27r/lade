package app.lade.calendar.ui.component.list.collapse

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import app.lade.agenda.api.agenda.AgendaModel
import java.time.LocalDate

@Composable
fun CalendarCollapseStrip(
    currentDate: LocalDate,
    entries: List<AgendaModel>,
    onDateSelected: (LocalDate) -> Unit,
    progress: Animatable<Float, *>,
    metrics: CalendarCollapseMetrics,
    config: CalendarCollapseConfig,
    modifier: Modifier = Modifier,
) {
    val progressValue by progress.asState()
    val datesWithEntries = remember(entries) { entries.map { it.date }.toSet() }
    val minAlpha = config.inactiveRowMinAlpha

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(metrics.stripHeight())
            .background(MaterialTheme.colorScheme.surface)
            .clipToBounds(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = metrics.columnOffsetY(progressValue)),
        ) {
            metrics.weeks.forEachIndexed { index, week ->
                val isActiveWeek = index == metrics.currentWeekIndex
                val rowAlpha = if (isActiveWeek) 1f
                else minAlpha + (1f - minAlpha) * progressValue
                CalendarCollapseMonthRow(
                    week = week,
                    currentDate = currentDate,
                    datesWithEntries = datesWithEntries,
                    onDateSelected = onDateSelected,
                    rowAlpha = rowAlpha,
                    horizontalPadding = config.rowHorizontalPadding,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(metrics.rowHeight),
                )
            }
        }
    }
}