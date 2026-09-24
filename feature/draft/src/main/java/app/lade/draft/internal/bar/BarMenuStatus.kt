package app.lade.draft.internal.bar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import app.lade.draft.R
import app.lade.draftdata.DraftModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
internal fun BarMenu.icon(): ImageVector = when (this) {
    BarMenu.Date -> Icons.Default.DateRange
    BarMenu.Time -> Icons.Default.Schedule
    BarMenu.Recurrence -> Icons.Default.Repeat
    BarMenu.Notifications -> Icons.Default.Notifications
    BarMenu.Goals -> Icons.Default.Flag
}

@Composable
internal fun BarMenu.status(draft: DraftModel): String = when (this) {
    BarMenu.Date -> draft.dateFrom?.let { formatDateStatus(draft) }
        ?: stringResource(R.string.draft_menu_date)

    BarMenu.Time -> draft.timeFrom?.let { formatTimeStatus(draft) }
        ?: stringResource(R.string.draft_menu_time)

    BarMenu.Recurrence -> draft.rrule?.let { formatRecurrenceStatus(it) }
        ?: stringResource(R.string.draft_menu_recurrence)

    BarMenu.Notifications -> formatNotificationsStatus(draft)
        ?: stringResource(R.string.draft_menu_notifications)

    BarMenu.Goals -> draft.goals.takeIf { it.isNotEmpty() }?.let {
        pluralStringResource(R.plurals.draft_status_goals, it.size, it.size)
    } ?: stringResource(R.string.draft_menu_goals)
}

@Composable
private fun formatDateStatus(draft: DraftModel): String {
    val from = draft.dateFrom ?: return ""
    val to = draft.dateTo
    val today = LocalDate.now()

    return if (to != null && to.isAfter(from)) {
        "${formatSingleDate(from, today)} – ${formatSingleDate(to, today)}"
    } else {
        formatSingleDate(from, today)
    }
}

@Composable
private fun formatSingleDate(date: LocalDate, today: LocalDate): String = when (date) {
    today -> stringResource(R.string.draft_status_date_today)
    today.plusDays(1) -> stringResource(R.string.draft_status_date_tomorrow)
    today.minusDays(1) -> stringResource(R.string.draft_status_date_yesterday)
    else -> rememberDateFormatter().format(date)
}

@Composable
private fun rememberDateFormatter(): DateTimeFormatter {
    val locale = LocalConfiguration.current.locales[0]
    return remember(locale) {
        DateTimeFormatter.ofPattern("d MMM", locale)
    }
}

@Composable
private fun formatTimeStatus(draft: DraftModel): String {
    val from = draft.timeFrom ?: return ""
    val to = draft.timeEnd
    val fmt = rememberTimeFormatter()

    return if (to != null) {
        "${from.format(fmt)}–${to.format(fmt)}"
    } else {
        from.format(fmt)
    }
}

@Composable
private fun rememberTimeFormatter(): DateTimeFormatter {
    val locale = LocalConfiguration.current.locales[0]
    return remember(locale) {
        DateTimeFormatter.ofPattern("HH:mm", locale)
    }
}

@Composable
private fun formatRecurrenceStatus(rrule: String): String? = when {
    rrule.contains("BYDAY=MO,TU,WE,TH,FR") -> stringResource(R.string.draft_status_recurrence_weekdays)
    rrule.contains("BYDAY=SA,SU") -> stringResource(R.string.draft_status_recurrence_weekends)
    rrule.contains("FREQ=DAILY") -> stringResource(R.string.draft_status_recurrence_daily)
    rrule.contains("FREQ=WEEKLY") -> stringResource(R.string.draft_status_recurrence_weekly)
    rrule.contains("FREQ=MONTHLY") -> stringResource(R.string.draft_status_recurrence_monthly)
    rrule.contains("FREQ=YEARLY") -> stringResource(R.string.draft_status_recurrence_yearly)
    else -> null
}

@Composable
private fun formatNotificationsStatus(draft: DraftModel): String? {
    val total = draft.alarms.size + draft.reminders.size
    if (total == 0) return null
    return pluralStringResource(R.plurals.draft_status_notifications, total, total)
}