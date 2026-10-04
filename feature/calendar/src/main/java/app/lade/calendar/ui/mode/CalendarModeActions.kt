package app.lade.calendar.ui.mode

import app.lade.calendar.domain.CalendarDateMode
import app.lade.calendar.domain.CalendarListStripMode
import app.lade.calendardata.api.CalendarCardModel
import java.time.LocalDate
import java.time.YearMonth

data class CalendarModeActions(
    val onSwipe: (CalendarDateMode, isMonth: Boolean) -> Unit,
    val onDateSelected: (LocalDate) -> Unit,
    val onEditEntry: (Long) -> Unit,
    val onOpenAgenda: (CalendarCardModel) -> Unit,
    val onToggleDone: (Long, LocalDate) -> Unit,
    val onGoalToggle: (Long, LocalDate, Long) -> Unit,
    val onOpenDay: (LocalDate) -> Unit,
    val onOpenMonth: (YearMonth) -> Unit,
    val onEntryLongPress: (CalendarCardModel) -> Unit,
    val onVisibleMonthChange: (YearMonth) -> Unit,
    val onTimelineScroll: (LocalDate) -> Unit,
)