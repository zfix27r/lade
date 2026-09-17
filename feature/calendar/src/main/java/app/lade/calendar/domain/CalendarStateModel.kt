package app.lade.calendar.domain

import app.lade.agenda.api.agenda.AgendaModel
import java.time.LocalDate

data class CalendarStateModel(
    val mode: CalendarMode = CalendarMode.LIST,
    val view: CalendarView = CalendarView.TIMELINE,
    val currentDate: LocalDate = LocalDate.now(),
    val entries: List<AgendaModel> = emptyList(),
    val stripMode: CalendarListStripMode = CalendarListStripMode.WEEK,
    val selectedSources: Set<String> = emptySet(),
)