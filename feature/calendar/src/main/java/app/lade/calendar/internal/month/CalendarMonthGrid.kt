package app.lade.calendar.internal.month

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import app.lade.calendardata.api.mark.CalendarDataMarkModel
import app.lade.resources.R
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.time.temporal.WeekFields

@Composable
internal fun CalendarMonthGrid(
    month: YearMonth,
    marks: Map<LocalDate, CalendarDataMarkModel>,
    selectedDate: LocalDate,
    onOpenDay: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val today = remember { LocalDate.now() }
    val firstDayOfWeek = WeekFields.of(LocalLocale.current.platformLocale).firstDayOfWeek
    val weekdays = (0..6).map { firstDayOfWeek.plus(it.toLong()) }
    val lead = ((month.atDay(1).dayOfWeek.value - firstDayOfWeek.value + 7) % 7)
    val cells = buildList {
        repeat(lead) { add(null) }
        for (day in 1..month.lengthOfMonth()) add(month.atDay(day))
        while (size % 7 != 0) add(null)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = dimensionResource(R.dimen.screen_padding)),
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            weekdays.forEach { day ->
                Text(
                    text = day.getDisplayName(TextStyle.NARROW, LocalLocale.current.platformLocale),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                )
            }
        }
        cells.chunked(7).forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_xs)),
            ) {
                week.forEach { date ->
                    val mark = date?.let { marks[it] }
                    CalendarMonthCell(
                        date = date,
                        isToday = date == today,
                        isSelected = date == selectedDate,
                        hasEntries = mark != null && mark.total > 0,
                        hasHabits = false,
                        onOpenDay = onOpenDay,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}