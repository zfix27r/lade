package app.lade.calendar.ui.mode

import app.lade.agenda.api.agenda.AgendaModel
import app.lade.calendar.domain.CalendarDateMode
import java.time.LocalDate
import java.time.YearMonth

data class CalendarModeActions(
    val onSwipe: (CalendarDateMode) -> Unit,
    val onDateSelected: (LocalDate) -> Unit,
    val onEditEntry: (Long) -> Unit,
    val onOpenAgenda: (AgendaModel) -> Unit,
    val onMarkDone: (Long, LocalDate) -> Unit,
    val onMarkSkip: (Long, LocalDate) -> Unit,
    val onOpenDay: (LocalDate) -> Unit,
    val onOpenMonth: (YearMonth) -> Unit,
    val onEntryLongPress: (AgendaModel) -> Unit,
)