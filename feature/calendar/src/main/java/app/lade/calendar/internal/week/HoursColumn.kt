package app.lade.calendar.internal.week

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.lade.calendar.api.config.WeekConfig
import app.lade.calendar.internal.week.data.GRID_HOUR_FROM
import app.lade.calendar.internal.week.data.GRID_HOUR_TO
import app.lade.ui.theme.Spacing

@Composable
internal fun HoursColumn(
    config: WeekConfig,
    modifier: Modifier = Modifier,
) {
    val hours = GRID_HOUR_FROM until GRID_HOUR_TO

    Column(modifier = modifier.wrapContentWidth()) {
        hours.forEach { hour ->
            Text(
                text = "%02d:00".format(hour),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .height(config.hourHeight)
                    .padding(end = Spacing.xs),
            )
        }
    }
}