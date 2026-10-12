package app.lade.calendardata.api.agenda

import java.time.LocalDate

data class CalendarDataDayModel(
    val date: LocalDate,
    val agendas: List<CalendarDataAgendaModel>,
)