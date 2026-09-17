package app.lade.calendar.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lade.agenda.api.AgendaApi
import app.lade.agenda.api.log.LogOrigin
import app.lade.agenda.api.log.LogSaveGoalModel
import app.lade.agenda.api.log.LogSaveModel
import app.lade.calendar.domain.CalendarDateMode
import app.lade.calendar.domain.CalendarListStripMode
import app.lade.calendar.domain.CalendarMode
import app.lade.calendar.domain.CalendarStateModel
import app.lade.calendar.domain.CalendarView
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.Locale
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val agendaApi: AgendaApi,
) : ViewModel() {

    private val _state = MutableStateFlow(CalendarStateModel())
    val state: StateFlow<CalendarStateModel> = _state.asStateFlow()

    private val weekFields = WeekFields.of(Locale.getDefault())

    init {
        observeEntries()
    }

    fun onModeChange(mode: CalendarMode) {
        _state.update { it.copy(mode = mode) }
    }

    fun onViewChange(view: CalendarView) {
        _state.update { it.copy(view = view) }
    }

    fun onSwipe(direction: CalendarDateMode) {
        val delta = when (direction) {
            CalendarDateMode.FORWARD -> 1L
            CalendarDateMode.BACKWARD -> -1L
        }
        val mode = _state.value.mode
        val newDate = when (mode) {
            CalendarMode.DAY -> _state.value.currentDate.plusDays(delta)
            CalendarMode.DAY_3 -> _state.value.currentDate.plusDays(delta * 3)
            CalendarMode.WEEK -> _state.value.currentDate.plusWeeks(delta)
            CalendarMode.MONTH -> _state.value.currentDate.plusMonths(delta)
            CalendarMode.YEAR -> _state.value.currentDate.plusYears(delta)
            CalendarMode.LIST -> _state.value.currentDate.plusDays(delta)
        }
        _state.update { it.copy(currentDate = newDate) }
    }

    fun onStripSwipe(mode: CalendarListStripMode) {
        _state.update { it.copy(stripMode = mode) }
    }

    fun onDateSelected(date: LocalDate) {
        _state.update { it.copy(currentDate = date) }
    }

    fun onToggleSource(source: String) {
        _state.update { state ->
            val next = if (source in state.selectedSources) {
                state.selectedSources - source
            } else {
                state.selectedSources + source
            }
            state.copy(selectedSources = next)
        }
    }

    fun onClearFilters() {
        _state.update { it.copy(selectedSources = emptySet()) }
    }

    fun markDone(entryId: Long, date: LocalDate) = mark(entryId, date, done = true)

    fun markSkip(entryId: Long, date: LocalDate) = mark(entryId, date, done = false)

    private fun mark(entryId: Long, date: LocalDate, done: Boolean) {
        viewModelScope.launch {
            val agenda = agendaApi.get(entryId, date) ?: return@launch
            val goals = agenda.goals.map { goal ->
                LogSaveGoalModel(
                    goalId = goal.id,
                    amount = if (done) goal.amount else 0,
                    repeat = if (done) goal.repeat else 0,
                    weight = goal.weight,
                )
            }
            val saveModel = LogSaveModel(
                date = date,
                goals = goals,
                origin = LogOrigin.CALENDAR,
            )
            agendaApi.saveLogs(saveModel)
        }
    }

    private fun observeEntries() {
        viewModelScope.launch {
            _state
                .flatMapLatest { state -> observeRange(state.currentDate, state.mode) }
                .collect { agendas ->
                    _state.update { it.copy(entries = agendas) }
                }
        }
    }

    private fun observeRange(anchor: LocalDate, mode: CalendarMode) = when (mode) {
        CalendarMode.LIST -> {
            agendaApi.observeList(anchor)
        }
        CalendarMode.DAY -> agendaApi.observeList(anchor)
        CalendarMode.DAY_3 -> agendaApi.observeRange(anchor, anchor.plusDays(2))
        CalendarMode.WEEK -> {
            val weekStart = anchor.with(weekFields.dayOfWeek(), 1L)
            agendaApi.observeRange(weekStart, weekStart.plusDays(6))
        }
        CalendarMode.MONTH -> {
            val from = anchor.withDayOfMonth(1)
            val to = from.plusMonths(1).minusDays(1)
            agendaApi.observeRange(from, to)
        }
        CalendarMode.YEAR -> {
            val from = anchor.withDayOfYear(1)
            val to = from.plusYears(1).minusDays(1)
            agendaApi.observeRange(from, to)
        }
    }

    fun goToday() {
        _state.update { it.copy(currentDate = LocalDate.now()) }
    }

    companion object {
        private const val FEED_PAST_DAYS = 60L
        private const val FEED_FUTURE_DAYS = 14L
    }
}