package app.lade.calendar.domain

data class CalendarSavedState(
    val viewName: String,
    val dateEpochDay: Long,
    val isStored: Boolean,
)