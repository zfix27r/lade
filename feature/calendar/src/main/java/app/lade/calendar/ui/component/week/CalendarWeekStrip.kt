package app.lade.calendar.ui.component.week

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import app.lade.agenda.api.agenda.AgendaModel
import app.lade.calendar.domain.CalendarMode
import app.lade.resources.R
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import androidx.compose.ui.platform.LocalLocale

@Composable
fun CalendarWeekStrip(
    mode: CalendarMode,
    currentDate: LocalDate,
    entries: List<AgendaModel>,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val days = daysFor(mode, currentDate)
    val today = LocalDate.now()
    val datesWithEntries = entries.map { it.date }.toSet()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(R.dimen.screen_padding),
                vertical = dimensionResource(R.dimen.spacing_sm),
            ),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_xs)),
    ) {
        days.forEach { date ->
            WeekStripCell(
                date = date,
                isSelected = date == currentDate,
                isToday = date == today,
                hasEntries = date in datesWithEntries,
                onClick = { onDateSelected(date) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun WeekStripCell(
    date: LocalDate,
    isSelected: Boolean,
    isToday: Boolean,
    hasEntries: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(dimensionResource(R.dimen.spacing_sm))
    val borderColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        isToday -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.outlineVariant
    }
    val container = when {
        isSelected -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surface
    }
    Column(
        modifier = modifier
            .clip(shape)
            .border(1.dp, borderColor, shape)
            .background(container)
            .clickable(onClick = onClick)
            .padding(vertical = dimensionResource(R.dimen.spacing_sm)),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = date.dayOfWeek.getDisplayName(TextStyle.NARROW, LocalLocale.current.platformLocale),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = date.dayOfMonth.toString(),
            style = MaterialTheme.typography.titleSmall,
            color = if (isSelected) {
                MaterialTheme.colorScheme.onPrimaryContainer
            } else {
                MaterialTheme.colorScheme.onSurface
            },
        )
        if (hasEntries) {
            Box(
                modifier = Modifier
                    .padding(top = dimensionResource(R.dimen.spacing_xs))
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
            )
        } else {
            Box(modifier = Modifier.size(4.dp))
        }
    }
}

private fun daysFor(mode: CalendarMode, anchor: LocalDate): List<LocalDate> {
    val weekFields = java.time.temporal.WeekFields.of(Locale.getDefault())
    return when (mode) {
        CalendarMode.DAY -> listOf(anchor)
        CalendarMode.DAY_3 -> (0L..2L).map { anchor.plusDays(it) }
        CalendarMode.WEEK -> {
            val weekStart = anchor.with(weekFields.dayOfWeek(), 1L)
            (0L..6L).map { weekStart.plusDays(it) }
        }
        else -> emptyList()
    }
}