package app.lade.calendar.internal.appbar.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CalendarViewWeek
import androidx.compose.material.icons.filled.ViewAgenda
import app.lade.calendar.internal.domain.mode.CalendarMode

internal fun CalendarMode.icon() = when (this) {
    CalendarMode.LIST -> Icons.AutoMirrored.Filled.ViewList
    CalendarMode.TIMELINE -> Icons.Filled.ViewAgenda
    CalendarMode.WEEK -> Icons.Filled.CalendarViewWeek
    CalendarMode.MONTH -> Icons.Filled.CalendarMonth
    CalendarMode.YEAR -> Icons.Filled.CalendarToday
}