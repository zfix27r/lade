package app.lade.calendar.internal.appbar.data

import app.lade.calendar.internal.domain.mode.CalendarMode
import app.lade.resources.R

internal fun CalendarMode.labelRes() = when (this) {
    CalendarMode.LIST -> R.string.calendar_view_feed
    CalendarMode.TIMELINE -> R.string.calendar_view_timeline
    CalendarMode.WEEK -> R.string.calendar_view_week
    CalendarMode.MONTH -> R.string.calendar_view_month
    CalendarMode.YEAR -> R.string.calendar_view_year
}