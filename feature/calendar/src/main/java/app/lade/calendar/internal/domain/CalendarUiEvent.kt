package app.lade.calendar.internal.domain

internal sealed interface CalendarUiEvent {
    data class Error(val message: String) : CalendarUiEvent
}