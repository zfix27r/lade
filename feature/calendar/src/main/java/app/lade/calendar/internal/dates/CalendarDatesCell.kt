package app.lade.calendar.internal.dates

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import app.lade.calendar.internal.dates.cell.CalendarDatesCellMarkView
import java.time.LocalDate

@Composable
internal fun CalendarDatesCell(
    date: LocalDate,
    selectedDate: LocalDate,
    markTotal: Int,
    onClick: (LocalDate) -> Unit,
    style: CalendarDatesStyle,
    modifier: Modifier = Modifier,
) {
    val isToday = isCalendarDatesToday(date)
    val isSelected = isCalendarDatesSelected(date, selectedDate)
    val hasMark = markTotal > 0

    val dayColor = when {
        isSelected -> style.selectedText
        isToday -> style.todayText
        else -> MaterialTheme.colorScheme.onSurface
    }
    val dayCircleModifier = when {
        isSelected -> Modifier
            .clip(CircleShape)
            .background(style.selectedFill)

        isToday -> Modifier
            .clip(CircleShape)
            .background(style.todayFill)

        else -> Modifier
    }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .padding(style.cellPadding)
            .clickable { onClick(date) },
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(style.dayCircleSize)
                    .then(dayCircleModifier),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = date.dayOfMonth.toString(),
                    style = style.dayTextStyle,
                    color = dayColor,
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(style.markerSpacing),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (hasMark) {
                    CalendarDatesCellMarkView(
                        color = style.markerColor,
                        size = style.markerDotSize,
                    )
                }
            }
        }
    }
}