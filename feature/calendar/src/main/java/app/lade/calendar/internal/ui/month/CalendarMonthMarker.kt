package app.lade.calendar.internal.ui.month

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import app.lade.resources.R

@Composable
internal fun CalendarMonthMarker(color: Color) {
    Box(
        modifier = Modifier
            .size(dimensionResource(R.dimen.calendar_marker_dot))
            .clip(CircleShape)
            .background(color),
    )
}