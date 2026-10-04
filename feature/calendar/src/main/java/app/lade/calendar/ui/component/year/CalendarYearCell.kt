package app.lade.calendar.ui.component.year

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import app.lade.resources.R

@Composable
internal fun CalendarYearCell(
    label: String,
    isCurrent: Boolean,
    hasEntries: Boolean,
    onClick: () -> Unit,
) {
    val bg = when {
        isCurrent -> MaterialTheme.colorScheme.primaryContainer
        hasEntries -> MaterialTheme.colorScheme.surfaceVariant
        else -> MaterialTheme.colorScheme.surface
    }
    val fg = when {
        isCurrent -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    Box(
        modifier = Modifier
            .aspectRatio(1.2f)
            .clip(RoundedCornerShape(dimensionResource(R.dimen.spacing_md)))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(dimensionResource(R.dimen.spacing_sm)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            color = fg,
            textAlign = TextAlign.Center,
        )
    }
}