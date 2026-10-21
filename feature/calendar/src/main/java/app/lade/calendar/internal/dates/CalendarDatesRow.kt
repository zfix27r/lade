package app.lade.calendar.internal.dates

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.text.style.TextAlign
import java.time.DayOfWeek
import java.time.format.TextStyle

@Composable
internal fun CalendarDatesRow(
    weekdays: List<DayOfWeek>,
    style: CalendarDatesStyle,
    modifier: Modifier = Modifier,
) {
    val locale = LocalLocale.current.platformLocale

    Row(modifier = modifier.fillMaxWidth()) {
        weekdays.forEach { day ->
            Text(
                text = day.getDisplayName(TextStyle.NARROW, locale),
                style = style.weekdayTextStyle,
                color = style.weekdayTextColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
            )
        }
    }
}