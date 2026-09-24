package app.lade.schedule.data

import app.lade.schedule.ui.AlarmModeOption
import app.lade.recurrence.api.RecurrenceDraft
import java.time.LocalDate
import java.time.LocalTime

data class TemporalOptions(
    val dateFrom: LocalDate? = null,
    val dateTo: LocalDate? = null,
    val time: LocalTime? = null,
    val timeEnd: LocalTime? = null,
    val recurrence: RecurrenceDraft = RecurrenceDraft(),
    val alarmMode: AlarmModeOption = AlarmModeOption.None,
    val reminderMinutesBefore: Int? = null,
)