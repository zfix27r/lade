package app.lade.calendar.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import app.lade.calendar.domain.FeedSource
import app.lade.resources.R

@Composable
fun feedSourceLabel(source: FeedSource): String = stringResource(
    when (source) {
        FeedSource.SCHEDULE -> R.string.calendar_source_schedule
        FeedSource.MANUAL -> R.string.calendar_source_manual
        FeedSource.HABIT -> R.string.calendar_source_habit
        FeedSource.HEALTH -> R.string.calendar_source_health
        FeedSource.CALENDAR -> R.string.calendar_source_calendar
    },
)