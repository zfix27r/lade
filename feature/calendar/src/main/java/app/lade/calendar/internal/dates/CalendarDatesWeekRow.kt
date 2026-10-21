package app.lade.calendar.internal.dates

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import java.time.LocalDate

/**
 * Строка недели: 7 ячеек дат одинаковой ширины.
 * Используется как базовая «неделя» универсальной сетки и как
 * стрип дат в недельном таймлайне (WeekLayout).
 */
@Composable
internal fun CalendarDatesWeekRow(
    week: CalendarDatesWeekModel,
    selectedDate: LocalDate,
    onDateClick: (LocalDate) -> Unit,
    style: CalendarDatesStyle,
    modifier: Modifier = Modifier,
) {
    println(week)

    Row(modifier = modifier.fillMaxWidth()) {
        week.days.forEach { day ->
            CalendarDatesCell(
                date = day.date,
                selectedDate = selectedDate,
                markTotal = day.mark?.total ?: 0,
                onClick = onDateClick,
                style = style,
                modifier = Modifier.weight(1f),
            )
        }
    }
}
