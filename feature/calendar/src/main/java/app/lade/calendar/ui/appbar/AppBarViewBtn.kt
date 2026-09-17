package app.lade.calendar.ui.appbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import app.lade.calendar.domain.CalendarView
import app.lade.resources.R

@Composable
fun AppBarViewBtn(
    view: CalendarView,
    onViewChange: (CalendarView) -> Unit,
) {
    val next = when (view) {
        CalendarView.TIMELINE -> CalendarView.GROUP_KIND
        CalendarView.GROUP_KIND -> CalendarView.TIMELINE
    }
    IconButton(onClick = { onViewChange(next) }) {
        Icon(
            imageVector = next.icon(),
            contentDescription = stringResource(next.labelRes()),
        )
    }
}

private fun CalendarView.icon() = when (this) {
    CalendarView.TIMELINE -> Icons.AutoMirrored.Filled.ViewList
    CalendarView.GROUP_KIND -> Icons.Filled.ViewAgenda
}

private fun CalendarView.labelRes() = when (this) {
    CalendarView.TIMELINE -> R.string.calendar_view_timeline
    CalendarView.GROUP_KIND -> R.string.calendar_view_group_kind
}