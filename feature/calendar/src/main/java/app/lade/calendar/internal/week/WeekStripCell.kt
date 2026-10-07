package app.lade.calendar.internal.week

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.platform.LocalLocale
import app.lade.calendar.api.config.WeekConfig
import java.time.LocalDate
import java.time.format.TextStyle

@Composable
internal fun WeekStripCell(
    date: LocalDate,
    isSelected: Boolean,
    isToday: Boolean,
    hasEntries: Boolean,
    onClick: () -> Unit,
    config: WeekConfig,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(config.cellCornerRadius)
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
            .border(config.cellBorderWidth, borderColor, shape)
            .background(container)
            .clickable(onClick = onClick)
            .padding(vertical = config.cellVerticalPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = date.dayOfWeek.getDisplayName(
                TextStyle.NARROW,
                LocalLocale.current.platformLocale
            ),
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
                    .padding(top = config.dotPadding)
                    .size(config.dotSize)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
            )
        } else {
            Box(modifier = Modifier.size(config.dotSize))
        }
    }
}