package app.lade.calendar.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.lade.calendar.api.config.CalendarConfig
import app.lade.calendar.api.config.DefaultCalendarConfig
import app.lade.calendar.internal.CalendarScreen
import app.lade.draft.api.DraftApi

@Composable
fun CalendarRoute(
    onOpenProfile: () -> Unit,
    draftApi: DraftApi,
    modifier: Modifier = Modifier,
    config: CalendarConfig = DefaultCalendarConfig,
) {
    CalendarScreen(
        onOpenProfile = onOpenProfile,
        draftApi = draftApi,
        config = config,
        modifier = modifier,
    )
}