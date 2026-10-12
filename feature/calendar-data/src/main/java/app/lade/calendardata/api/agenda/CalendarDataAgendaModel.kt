package app.lade.calendardata.api.agenda

data class CalendarDataAgendaModel(
    val entry: CalendarDataEntryModel,
    val goals: CalendarDataGoalsModel?,
    val timer: CalendarDataTimerModel?,
    val notifications: CalendarDataNotificationsModel?,
    val daysLeft: Int?,
)