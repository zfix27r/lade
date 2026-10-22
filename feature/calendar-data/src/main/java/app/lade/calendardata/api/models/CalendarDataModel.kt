package app.lade.calendardata.api.models

data class CalendarDataModel(
    val entry: CalendarDataEntryModel,
    val goals: CalendarDataGoalsModel?,
    val timer: CalendarDataTimerModel?,
    val notifications: CalendarDataNotificationsModel?,
)