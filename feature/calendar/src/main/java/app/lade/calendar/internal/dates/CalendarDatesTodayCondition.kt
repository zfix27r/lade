package app.lade.calendar.internal.dates

import androidx.compose.runtime.Composable
import app.lade.ui.theme.LocalToday
import java.time.LocalDate

@Composable
internal fun isCalendarDatesToday(date: LocalDate): Boolean =
    date == LocalToday.current