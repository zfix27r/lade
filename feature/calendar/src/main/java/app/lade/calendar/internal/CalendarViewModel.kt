package app.lade.calendar.internal

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lade.calendar.api.config.DefaultTimelineWindowConfig
import app.lade.calendar.api.config.TimelineWindowConfig
import app.lade.calendar.internal.data.MarkedDatesStore
import app.lade.calendar.internal.data.calendarRange
import app.lade.calendar.internal.data.shiftCalendarDate
import app.lade.calendar.internal.data.timelineWindow
import app.lade.calendar.internal.main.CalendarDateMode
import app.lade.calendar.internal.main.CalendarMode
import app.lade.calendar.internal.main.CalendarStateModel
import app.lade.calendar.internal.main.CalendarUiEvent
import app.lade.calendardata.api.CalendarDataApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.Locale
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
internal class CalendarViewModel @Inject constructor(
    private val calendarData: CalendarDataApi,
    internal val markedDatesStore: MarkedDatesStore,
    private val savedStateHandle: SavedStateHandle,
    private val timelineConfig: TimelineWindowConfig = DefaultTimelineWindowConfig,
) : ViewModel() {

    private val keyDate = "calendar.currentDate"
    private val keyMode = "calendar.mode"

    private val initialDate = savedStateHandle.get<String>(keyDate)?.let(LocalDate::parse)
        ?: LocalDate.now()

    private val _state = MutableStateFlow(
        CalendarStateModel(
            mode = savedStateHandle.get<String>(keyMode)
                ?.let { runCatching { CalendarMode.valueOf(it) }.getOrNull() }
                ?: CalendarMode.LIST,
            currentDate = initialDate,
            days = emptyList(),
            marks = emptyMap(),
        )
    )
    val state: StateFlow<CalendarStateModel> = _state.asStateFlow()

    private val _events = MutableSharedFlow<CalendarUiEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<CalendarUiEvent> = _events.asSharedFlow()

    private val weekFields: WeekFields = WeekFields.of(Locale.getDefault())

    private var daysJob: Job? = null
    private var timelineJob: Job? = null

    private var timelineWindow = timelineWindow(_state.value.currentDate, timelineConfig)

    private var markedDatesStoreStarted = false

    init {
        observeModeAndDate()
        observeMarkedDates()
        persistState()
    }

    fun onModeChange(mode: CalendarMode) {
        _state.update { it.copy(mode = mode) }
        reload()
    }

    fun onDateSelected(date: LocalDate) {
        _state.update { it.copy(currentDate = date) }
    }

    fun goToday() {
        _state.update { it.copy(currentDate = LocalDate.now()) }
    }

    fun onTimelineScroll(firstVisibleDate: LocalDate) {
        if (_state.value.mode != CalendarMode.TIMELINE) return
        val window = timelineWindow
        var next = window
        if (window.isNearStart(firstVisibleDate, timelineConfig.loadThresholdDays)) {
            next = next.growsStart(timelineConfig.pageDays)
        }
        if (window.isNearEnd(firstVisibleDate, timelineConfig.loadThresholdDays)) {
            next = next.growsEnd(timelineConfig.pageDays)
        }
        if (next != window) {
            timelineWindow = next
            observeTimeline()
        }
    }

    fun onSwipe(direction: CalendarDateMode, isMonth: Boolean) {
        if (_state.value.mode == CalendarMode.LIST) return
        val delta = when (direction) {
            CalendarDateMode.FORWARD -> 1L
            CalendarDateMode.BACKWARD -> -1L
        }
        _state.update { s ->
            s.copy(currentDate = shiftCalendarDate(s.currentDate, s.mode, delta))
        }
    }

    fun toggleDone(entryId: Long, date: LocalDate) {
        viewModelScope.launch {
            runCatching { calendarData.toggleAllGoals(entryId, date) }
                .onFailure { _events.tryEmit(CalendarUiEvent.Error("Failed to toggle")) }
        }
    }

    fun toggleGoal(entryId: Long, date: LocalDate, goalId: Long) {
        viewModelScope.launch {
            runCatching { calendarData.toggleGoal(entryId, date, goalId) }
                .onFailure { _events.tryEmit(CalendarUiEvent.Error("Failed to toggle goal")) }
        }
    }

    fun markSkip(entryId: Long, date: LocalDate) {
        viewModelScope.launch {
            runCatching { calendarData.skipAllGoals(entryId, date) }
                .onFailure { _events.tryEmit(CalendarUiEvent.Error("Failed to skip")) }
        }
    }

    fun archiveEntry(entryId: Long) {
        _events.tryEmit(CalendarUiEvent.Error("Not implemented"))
    }

    fun setGoalAmount(entryId: Long, date: LocalDate, goalId: Long, amount: Int) {
        viewModelScope.launch {
            runCatching { calendarData.setGoalAmount(entryId, date, goalId, amount) }
                .onFailure {
                    _events.tryEmit(CalendarUiEvent.Error("Failed to set goal amount"))
                }
        }
    }

    fun startTimer(entryId: Long, date: LocalDate) {
        viewModelScope.launch {
            runCatching {
                calendarData.startTimer(entryId, date, System.currentTimeMillis())
            }.onFailure {
                _events.tryEmit(CalendarUiEvent.Error("Failed to start timer"))
            }
        }
    }

    fun finishTimer(entryId: Long, date: LocalDate, actualMinutes: Int) {
        viewModelScope.launch {
            runCatching {
                calendarData.finishTimer(
                    entryId = entryId,
                    date = date,
                    endedAtMs = System.currentTimeMillis(),
                    actualMinutes = actualMinutes,
                )
            }.onFailure {
                _events.tryEmit(CalendarUiEvent.Error("Failed to finish timer"))
            }
        }
    }

    private fun observeModeAndDate() {
        viewModelScope.launch {
            _state
                .map { it.mode to it.currentDate }
                .distinctUntilChanged()
                .collect { (mode, date) ->
                    when (mode) {
                        CalendarMode.TIMELINE -> {
                            timelineWindow = timelineWindow(date, timelineConfig)
                            observeTimeline()
                        }

                        else -> observeDays(date, mode)
                    }
                }
        }
    }

    private fun observeMarkedDates() {
        viewModelScope.launch {
            _state
                .map { it.currentDate }
                .distinctUntilChanged()
                .collect { date ->
                    if (!markedDatesStoreStarted) {
                        markedDatesStore.start(date)
                        markedDatesStoreStarted = true
                    } else {
                        markedDatesStore.setAnchor(date)
                    }
                }
        }
        viewModelScope.launch {
            markedDatesStore.marked.collect { marks ->
                _state.update { it.copy(marks = marks) }
            }
        }
    }

    private fun reload() {
        val s = _state.value
        when (s.mode) {
            CalendarMode.TIMELINE -> observeTimeline()
            else -> observeDays(s.currentDate, s.mode)
        }
    }

    private fun observeDays(date: LocalDate, mode: CalendarMode) {
        daysJob?.cancel()
        daysJob = viewModelScope.launch {
            val range = calendarRange(date, mode, weekFields) ?: return@launch
            calendarData.observeRangeDays(range.start, range.endInclusive)
                .catch { t -> _events.tryEmit(CalendarUiEvent.Error(t.message ?: "Load error")) }
                .collect { days -> _state.update { it.copy(days = days) } }
        }
    }

    private fun observeTimeline() {
        timelineJob?.cancel()
        timelineJob = viewModelScope.launch {
            val window = timelineWindow
            calendarData.observeRangeDays(window.start, window.end)
                .catch { t ->
                    _events.tryEmit(CalendarUiEvent.Error(t.message ?: "Load error"))
                }
                .collect { days -> _state.update { it.copy(days = days) } }
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
    }
}