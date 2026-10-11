package app.lade.calendar.internal.list.card

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import app.lade.calendar.R

@Composable
internal fun CardDaysLeftBadge(
    daysLeft: Int,
    modifier: Modifier = Modifier,
) {
    val text = when (daysLeft) {
        0 -> stringResource(R.string.calendar_days_left_today)
        else -> pluralStringResource(
            R.plurals.calendar_days_left,
            daysLeft,
            daysLeft,
        )
    }

    val textColor = if (daysLeft <= 3) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = textColor,
        modifier = modifier,
    )
}