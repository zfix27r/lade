package app.lade.calendar.internal.list.strip.port

import androidx.compose.runtime.State
import app.lade.calendardata.api.DayProgress
import java.time.LocalDate

internal data class StripPortModel(
    val calendarDate: State<LocalDate>,
    val calendarMarkedDates: State<Map<LocalDate, DayProgress>>,
)