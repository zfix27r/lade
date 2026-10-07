package app.lade.calendar.internal.domain.mode

import app.lade.calendar.internal.domain.CalendarDateMode
import app.lade.calendardata.api.CalendarCardModel
import java.time.LocalDate
import java.time.YearMonth

internal data class CalendarModeActions(
    val onSwipe: (CalendarDateMode, isMonth: Boolean) -> Unit,
    val onDateSelected: (LocalDate) -> Unit,
    val onEditEntry: (Long) -> Unit,
    val onOpenAgenda: (CalendarCardModel) -> Unit,
    val onToggleDone: (Long, LocalDate) -> Unit,
    val onGoalToggle: (Long, LocalDate, Long) -> Unit,
    val onOpenDay: (LocalDate) -> Unit,
    val onOpenMonth: (YearMonth) -> Unit,
    val onEntryLongPress: (CalendarCardModel) -> Unit,
    val onTimelineScroll: (LocalDate) -> Unit,
)