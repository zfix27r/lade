package app.lade.calendar.ui.mode

import app.lade.calendar.domain.CalendarDateMode
import java.time.LocalDate
import java.time.YearMonth

data class CalendarModeActions(
    val onSwipe: (CalendarDateMode) -> Unit,
    val onDateSelected: (LocalDate) -> Unit,
    val onEditEntry: (entryId: Long) -> Unit,
    val onMarkDone: (entryId: Long, date: LocalDate) -> Unit,
    val onMarkSkip: (entryId: Long, date: LocalDate) -> Unit,
    val onOpenDay: (LocalDate) -> Unit,
    val onOpenMonth: (YearMonth) -> Unit,
)