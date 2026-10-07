package app.lade.calendar.internal.week

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLocale
import app.lade.calendar.api.config.WeekConfig
import app.lade.calendar.internal.week.data.weekDays
import app.lade.calendardata.api.CalendarCardModel
import java.time.LocalDate
import java.time.temporal.WeekFields

@Composable
internal fun WeekStrip(
    currentDate: LocalDate,
    entries: List<CalendarCardModel>,
    onDateSelected: (LocalDate) -> Unit,
    config: WeekConfig,
    modifier: Modifier = Modifier,
) {
    val weekFields = WeekFields.of(LocalLocale.current.platformLocale)
    val days = weekDays(currentDate, weekFields)
    val today = LocalDate.now()
    val datesWithEntries = entries.map { it.date }.toSet()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = config.horizontalPadding,
                vertical = config.verticalPadding,
            ),
        horizontalArrangement = Arrangement.spacedBy(config.cellSpacing),
    ) {
        days.forEach { date ->
            WeekStripCell(
                date = date,
                isSelected = date == currentDate,
                isToday = date == today,
                hasEntries = date in datesWithEntries,
                onClick = { onDateSelected(date) },
                config = config,
                modifier = Modifier.weight(1f),
            )
        }
    }
}