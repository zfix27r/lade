package app.lade.calendar.internal.main

internal sealed interface CalendarUiEvent {
    data class Error(val message: String) : CalendarUiEvent
}