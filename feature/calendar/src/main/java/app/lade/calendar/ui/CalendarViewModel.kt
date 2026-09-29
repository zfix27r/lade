package app.lade.calendar.ui

import androidx.lifecycle.SavedStateHandle
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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
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
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val keyDate = "calendar.currentDate"
    private val keyMode = "calendar.mode"
    private val keyView = "calendar.view"
    private val keyStrip = "calendar.stripMode"

    private val _state = MutableStateFlow(
        CalendarStateModel(
            currentDate = savedStateHandle.get<String>(keyDate)?.let(LocalDate::parse)
                ?: LocalDate.now(),
            mode = savedStateHandle.get<String>(keyMode)
                ?.let { runCatching { CalendarMode.valueOf(it) }.getOrNull() }
                ?: CalendarMode.LIST,
            view = savedStateHandle.get<String>(keyView)
                ?.let { runCatching { CalendarView.valueOf(it) }.getOrNull() }
                ?: CalendarView.TIMELINE,
            stripMode = savedStateHandle.get<String>(keyStrip)
                ?.let { runCatching { CalendarListStripMode.valueOf(it) }.getOrNull() }
                ?: CalendarListStripMode.WEEK,
        )
    )
    val state: StateFlow<CalendarStateModel> = _state.asStateFlow()

    private val _events = MutableSharedFlow<CalendarUiEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<CalendarUiEvent> = _events.asSharedFlow()

    private val weekFields: WeekFields = WeekFields.of(Locale.getDefault())

    init {
        observeEntries()
        persistState()
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
        _state.update { s ->
            val newDate = when (s.mode) {
                CalendarMode.DAY, CalendarMode.LIST -> s.currentDate.plusDays(delta)
                CalendarMode.DAY_3 -> s.currentDate.plusDays(delta * 3)
                CalendarMode.WEEK -> s.currentDate.plusWeeks(delta)
                CalendarMode.MONTH -> {
                    val target = s.currentDate.withDayOfMonth(1).plusMonths(delta)
                    target.withDayOfMonth(
                        s.currentDate.dayOfMonth.coerceAtMost(target.lengthOfMonth())
                    )
                }
                CalendarMode.YEAR -> {
                    val target = s.currentDate.withDayOfYear(1).plusYears(delta)
                    target.withDayOfYear(
                        s.currentDate.dayOfYear.coerceAtMost(target.lengthOfYear())
                    )
                }
            }
            s.copy(currentDate = newDate)
        }
    }

    fun onDateSelected(date: LocalDate) {
        _state.update { it.copy(currentDate = date) }
    }

    fun goToday() {
        _state.update { it.copy(currentDate = LocalDate.now()) }
    }

    fun archiveEntry(entryId: Long) {
        viewModelScope.launch {
            runCatching { agendaApi.archiveEntry(entryId) }
                .onFailure { _events.tryEmit(CalendarUiEvent.Error("Archive failed")) }
        }
    }

    fun toggleDone(entryId: Long, date: LocalDate) {
        viewModelScope.launch {
            runCatching {
                val agenda = agendaApi.get(entryId, date) ?: return@runCatching
                val currentlyDone = agenda.logs.any { (it.actualAmount ?: 0) > 0 }
                val goals = agenda.goals.map { goal ->
                    LogSaveGoalModel(
                        goalId = goal.id,
                        amount = if (currentlyDone) 0 else goal.amount,
                        repeat = if (currentlyDone) 0 else goal.repeat,
                        weight = goal.weight,
                    )
                }
                agendaApi.saveLogs(
                    LogSaveModel(
                        date = date,
                        goals = goals,
                        origin = LogOrigin.CALENDAR,
                    )
                )
            }.onFailure {
                _events.tryEmit(CalendarUiEvent.Error("Failed to save log"))
            }
        }
    }

    fun markSkip(entryId: Long, date: LocalDate) = mark(entryId, date, done = false)

    private fun mark(entryId: Long, date: LocalDate, done: Boolean) {
        viewModelScope.launch {
            runCatching {
                val agenda = agendaApi.get(entryId, date) ?: return@runCatching
                val goals = agenda.goals.map { goal ->
                    LogSaveGoalModel(
                        goalId = goal.id,
                        amount = if (done) goal.amount else 0,
                        repeat = if (done) goal.repeat else 0,
                        weight = goal.weight,
                    )
                }
                agendaApi.saveLogs(
                    LogSaveModel(
                        date = date,
                        goals = goals,
                        origin = LogOrigin.CALENDAR,
                    )
                )
            }.onFailure {
                _events.tryEmit(CalendarUiEvent.Error("Failed to save log"))
            }
        }
    }

    private fun observeEntries() {
        viewModelScope.launch {
            _state
                .map { it.currentDate to it.mode }
                .distinctUntilChanged()
                .flatMapLatest { (date, mode) ->
                    observeRange(date, mode)
                        .onStart { _state.update { it.copy(isLoading = true, error = null) } }
                        .catch { t ->
                            _state.update {
                                it.copy(isLoading = false, error = t.message ?: "Load error")
                            }
                            emit(emptyList())
                        }
                }
                .collect { agendas ->
                    _state.update { it.copy(entries = agendas, isLoading = false) }
                }
        }
    }

    private fun observeRange(
        anchor: LocalDate,
        mode: CalendarMode,
    ) = when (mode) {
        CalendarMode.LIST, CalendarMode.DAY -> agendaApi.observeList(anchor)
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

    private fun persistState() {
        viewModelScope.launch {
            _state.map { it.currentDate }.distinctUntilChanged()
                .collect { savedStateHandle[keyDate] = it.toString() }
        }
        viewModelScope.launch {
            _state.map { it.mode }.distinctUntilChanged()
                .collect { savedStateHandle[keyMode] = it.name }
        }
        viewModelScope.launch {
            _state.map { it.view }.distinctUntilChanged()
                .collect { savedStateHandle[keyView] = it.name }
        }
        viewModelScope.launch {
            _state.map { it.stripMode }.distinctUntilChanged()
                .collect { savedStateHandle[keyStrip] = it.name }
        }
    }
}

sealed interface CalendarUiEvent {
    data class Error(val message: String) : CalendarUiEvent
}