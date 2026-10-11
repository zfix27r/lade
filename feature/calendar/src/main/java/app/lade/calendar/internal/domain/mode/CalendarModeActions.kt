package app.lade.calendar.internal.domain.mode

import app.lade.calendar.internal.domain.CalendarDateMode
import app.lade.calendardata.api.CalendarCardModel
import java.time.LocalDate
import java.time.YearMonth

internal data class CalendarModeActions(
    val onSwipe: (CalendarDateMode, Boolean) -> Unit,
    val onDateSelected: (LocalDate) -> Unit,
    val onEditEntry: (Long) -> Unit,
    val onOpenAgenda: (CalendarCardModel) -> Unit,
    val onEntryLongPress: (CalendarCardModel) -> Unit,
    val onToggleDone: (Long, LocalDate) -> Unit,
    val onGoalToggle: (Long, LocalDate, Long) -> Unit,
    val onGoalValueChange: (Long, LocalDate, Long, Int) -> Unit = { _, _, _, _ -> },
    val onLoadGoalDetails: (Long, LocalDate) -> Unit = { _, _ -> },
    val onStartTimer: (Long, LocalDate) -> Unit = { _, _ -> },
    val onFinishTimer: (Long, LocalDate, Int) -> Unit = { _, _, _ -> },
    val onOpenDay: (LocalDate) -> Unit,
    val onOpenMonth: (YearMonth) -> Unit,
    val onTimelineScroll: (LocalDate) -> Unit,
)