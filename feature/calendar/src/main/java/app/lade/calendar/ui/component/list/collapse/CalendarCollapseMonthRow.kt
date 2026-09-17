package app.lade.calendar.ui.component.list.collapse

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.time.LocalDate

@Composable
fun CalendarCollapseMonthRow(
    week: List<LocalDate?>,
    currentDate: LocalDate,
    datesWithEntries: Set<LocalDate>,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    rowAlpha: Float = 1f,
    horizontalPadding: Dp = 0.dp,
) {
    val today = LocalDate.now()
    Row(
        modifier = modifier
            .alpha(rowAlpha)
            .padding(horizontal = horizontalPadding),
    ) {
        week.forEach { date ->
            val cellModifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .then(
                    if (date != null) Modifier.clickable { onDateSelected(date) }
                    else Modifier,
                )
            Box(
                modifier = cellModifier,
                contentAlignment = Alignment.Center,
            ) {
                if (date != null) {
                    Text(
                        text = date.dayOfMonth.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = when (date) {
                            currentDate -> MaterialTheme.colorScheme.primary
                            today -> MaterialTheme.colorScheme.tertiary
                            in datesWithEntries -> MaterialTheme.colorScheme.onSurface
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                    )
                }
            }
        }
    }
}