package app.lade.calendar.internal.timeline

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.text.style.TextAlign
import app.lade.calendar.api.config.TimelineLayoutConfig
import app.lade.calendar.internal.timeline.data.TimelineDay
import java.time.format.TextStyle

@Composable
internal fun TimelineDateRail(
    day: TimelineDay,
    isToday: Boolean,
    config: TimelineLayoutConfig,
    modifier: Modifier = Modifier,
) {
    val locale = LocalLocale.current.platformLocale
    val weekday = day.date.dayOfWeek.getDisplayName(TextStyle.SHORT, locale)
    val dayNumber = day.date.dayOfMonth.toString()

    Column(
        modifier = modifier
            .width(config.railWidth)
            .padding(horizontal = config.railPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = weekday,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            maxLines = 1,
        )
        Box(
            modifier = Modifier
                .padding(top = config.dayPadding)
                .height(config.dayCircleSize)
                .width(config.dayCircleSize)
                .clip(CircleShape)
                .background(
                    if (isToday) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surface
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = dayNumber,
                style = MaterialTheme.typography.titleSmall,
                color = if (isToday) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
        }
    }
}