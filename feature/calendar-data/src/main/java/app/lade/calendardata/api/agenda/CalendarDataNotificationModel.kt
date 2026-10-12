package app.lade.calendardata.api.agenda

import java.time.LocalDate
import java.time.LocalTime

data class CalendarDataNotificationModel(
    val id: Long,
    val date: LocalDate,
    val time: LocalTime,
    val isActive: Boolean,
)