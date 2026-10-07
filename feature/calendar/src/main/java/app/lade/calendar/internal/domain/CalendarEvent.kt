package app.lade.calendar.internal.domain

import app.lade.calendar.internal.domain.mode.CalendarMode
import java.time.LocalDate

internal sealed interface CalendarEvent {
    data class Swipe(val direction: CalendarDateMode, val isMonth: Boolean) : CalendarEvent
    data class TapDate(val date: LocalDate) : CalendarEvent
    data object TapToday : CalendarEvent
    data class ChangeMode(val mode: CalendarMode) : CalendarEvent
    data class TimelineScrolled(val firstVisibleDate: LocalDate) : CalendarEvent
    data class ToggleDone(val entryId: Long, val date: LocalDate) : CalendarEvent
    data class ToggleGoal(val entryId: Long, val date: LocalDate, val goalId: Long) : CalendarEvent
}