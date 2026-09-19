package app.lade.draft.internal.bar.conf

import androidx.lifecycle.ViewModel
import app.lade.draft.DraftAlarm
import app.lade.draft.DraftReminder
import app.lade.draft.internal.DraftApiImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

internal data class ConfSheetState(
    val dateFrom: LocalDate? = null,
    val dateTo: LocalDate? = null,
    val timeFrom: LocalTime? = null,
    val timeEnd: LocalTime? = null,
    val recurrence: DraftRecurrencePreset = DraftRecurrencePreset.NONE,
    val reminders: List<DraftReminder> = emptyList(),
    val alarms: List<DraftAlarm> = emptyList(),
    val isRangeDate: Boolean = false,
    val isRangeTime: Boolean = false,
)

@HiltViewModel
internal class DraftBarConfSheetViewModel @Inject constructor(
    private val api: DraftApiImpl,
) : ViewModel() {

    private val _state = MutableStateFlow(ConfSheetState())
    val state: StateFlow<ConfSheetState> = _state.asStateFlow()

    init {
        val draft = api.draft.value
        _state.value = ConfSheetState(
            dateFrom = draft.dateFrom,
            dateTo = draft.dateTo,
            timeFrom = draft.timeFrom,
            timeEnd = draft.timeEnd,
            recurrence = DraftRecurrencePreset.fromRrule(draft.rrule),
            reminders = draft.reminders,
            alarms = draft.alarms,
            isRangeDate = draft.dateTo != null,
            isRangeTime = draft.timeEnd != null,
        )
    }

    fun onDateFromChange(date: LocalDate?) {
        _state.update { it.copy(dateFrom = date) }
    }

    fun onDateToChange(date: LocalDate?) {
        _state.update { it.copy(dateTo = date) }
    }

    fun onTimeFromChange(time: LocalTime?) {
        _state.update { it.copy(timeFrom = time) }
    }

    fun onTimeEndChange(time: LocalTime?) {
        _state.update { it.copy(timeEnd = time) }
    }

    fun onRecurrenceChange(preset: DraftRecurrencePreset) {
        _state.update { it.copy(recurrence = preset) }
    }

    fun onRemindersChange(reminders: List<DraftReminder>) {
        _state.update { it.copy(reminders = reminders) }
    }

    fun onAlarmsChange(alarms: List<DraftAlarm>) {
        _state.update { it.copy(alarms = alarms) }
    }

    fun onRangeDateToggle(enabled: Boolean) {
        _state.update {
            it.copy(
                isRangeDate = enabled,
                dateTo = if (enabled) it.dateTo else null,
            )
        }
    }

    fun onRangeTimeToggle(enabled: Boolean) {
        _state.update {
            it.copy(
                isRangeTime = enabled,
                timeEnd = if (enabled) it.timeEnd else null,
            )
        }
    }

    fun onApply() {
        val s = _state.value
        api.updateDate(s.dateFrom, if (s.isRangeDate) s.dateTo else null)
        api.updateTime(s.timeFrom, if (s.isRangeTime) s.timeEnd else null)
        api.updateRecurrence(s.recurrence.rrule)
        api.updateReminders(s.reminders)
        api.updateAlarms(s.alarms)
    }
}