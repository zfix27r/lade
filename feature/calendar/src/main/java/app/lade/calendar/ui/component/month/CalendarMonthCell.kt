package app.lade.calendar.ui.component.month

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import app.lade.resources.R
import java.time.LocalDate

@Composable
internal fun CalendarMonthCell(
    date: LocalDate?,
    isToday: Boolean,
    isSelected: Boolean,
    hasEntries: Boolean,
    hasHabits: Boolean,
    onOpenDay: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val minSize = dimensionResource(R.dimen.min_touch_target)
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(dimensionResource(R.dimen.spacing_xs))
            .then(
                if (date != null) {
                    Modifier.clickable { onOpenDay(date) }
                } else {
                    Modifier
                },
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (date != null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(minSize * 0.7f)
                        .then(
                            when {
                                isSelected -> Modifier
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)

                                isToday -> Modifier
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer)

                                else -> Modifier
                            },
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = date.dayOfMonth.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = when {
                            isSelected -> MaterialTheme.colorScheme.onPrimary
                            isToday -> MaterialTheme.colorScheme.onPrimaryContainer
                            else -> MaterialTheme.colorScheme.onSurface
                        },
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_xs)),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (hasEntries) {
                        CalendarMonthMarker(color = MaterialTheme.colorScheme.primary)
                    }
                    if (hasHabits) {
                        CalendarMonthMarker(color = MaterialTheme.colorScheme.tertiary)
                    }
                }
            }
        }
    }
}