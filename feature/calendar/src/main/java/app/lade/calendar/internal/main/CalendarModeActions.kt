package app.lade.calendar.internal.main

import app.lade.calendardata.api.agenda.CalendarDataAgendaModel
import java.time.LocalDate
import java.time.YearMonth

data class CalendarModeActions(
    val onSwipe: (CalendarDateMode, Boolean) -> Unit,
    val onDateSelected: (LocalDate) -> Unit,
    val onVisibleDateChange: (LocalDate) -> Unit,
    val onEditEntry: (Long) -> Unit,
    val onOpenAgenda: (LocalDate, CalendarDataAgendaModel) -> Unit,
    val onEntryLongPress: (LocalDate, CalendarDataAgendaModel) -> Unit,
    val onToggleDone: (Long, LocalDate) -> Unit,
    val onGoalToggle: (Long, LocalDate, Long) -> Unit,
    val onGoalValueChange: (Long, LocalDate, Long, Int) -> Unit = { _, _, _, _ -> },
    val onStartTimer: (Long, LocalDate) -> Unit = { _, _ -> },
    val onFinishTimer: (Long, LocalDate, Int) -> Unit = { _, _, _ -> },
    val onOpenDay: (LocalDate) -> Unit,
    val onOpenMonth: (YearMonth) -> Unit,
    val onTimelineScroll: (LocalDate) -> Unit = {},
)