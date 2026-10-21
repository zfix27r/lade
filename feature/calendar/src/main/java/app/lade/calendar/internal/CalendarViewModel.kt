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
import app.lade.calendar.internal.dates.CalendarDatesWeekModel
import app.lade.calendar.internal.dates.grid.buildCalendarDatesWeeks
import app.lade.calendar.internal.main.CalendarDateMode
import app.lade.calendar.internal.main.CalendarMode
import app.lade.calendar.internal.main.CalendarUiEvent
import app.lade.calendardata.api.CalendarDataApi
import app.lade.calendardata.api.agenda.CalendarDataDayModel
import app.lade.calendardata.api.mark.CalendarDataMarkModel
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
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

    private val keyDate = "calendar.selectedDate"
    private val keyMode = "calendar.mode"

    private val initialMode: CalendarMode = savedStateHandle.get<String>(keyMode)
        ?.let { runCatching { CalendarMode.valueOf(it) }.getOrNull() }
        ?: CalendarMode.WEEK

    private val initialDate: LocalDate = savedStateHandle.get<String>(keyDate)
        ?.let(LocalDate::parse)
        ?: LocalDate.now()

    private val _mode = MutableStateFlow(initialMode)
    val mode: StateFlow<CalendarMode> = _mode.asStateFlow()

    private val _selectedDate = MutableStateFlow(initialDate)
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _visibleDate = MutableStateFlow(initialDate)
    val visibleDate: StateFlow<LocalDate> = _visibleDate.asStateFlow()

    private val _days = MutableStateFlow(emptyList<CalendarDataDayModel>())
    val days: StateFlow<List<CalendarDataDayModel>> = _days.asStateFlow()

    private val _marks = MutableStateFlow(emptyMap<LocalDate, CalendarDataMarkModel>())
    val marks: StateFlow<Map<LocalDate, CalendarDataMarkModel>> = _marks.asStateFlow()

    private val _events = MutableSharedFlow<CalendarUiEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<CalendarUiEvent> = _events.asSharedFlow()

    private val weekFields: WeekFields = WeekFields.of(Locale.getDefault())

    private var daysJob: Job? = null
    private var timelineJob: Job? = null

    private var timelineWindow = timelineWindow(initialDate, timelineConfig)

    private var markedDatesStoreStarted = false

    private val _weeks = MutableStateFlow<List<CalendarDatesWeekModel>>(emptyList())
    val weeks: StateFlow<List<CalendarDatesWeekModel>> = _weeks.asStateFlow()


    init {
        observeWeeks()
        observeModeAndVisibleDate()
        observeMarkedDates()
        persistState()
    }

    private fun observeWeeks() {
        val today = LocalDate.now()
        val from = today.minusYears(RANGE_YEARS_BACK)
        val to = today.plusYears(RANGE_YEARS_FORWARD)
        val weekFields = WeekFields.of(Locale.getDefault())

        viewModelScope.launch {
            calendarData.observeMarkedDates(from, to)
                .catch { t -> _events.tryEmit(CalendarUiEvent.Error(t.message ?: "Load error")) }
                .collect { marks ->
                    _weeks.value = buildCalendarDatesWeeks(from, to, marks, weekFields)
                }
        }
    }

    fun onModeChange(mode: CalendarMode) {
        _mode.value = mode
        reload()
    }

    fun onDateSelected(date: LocalDate) {
        _selectedDate.value = date
        _visibleDate.value = date
    }

    fun onVisibleDateChange(date: LocalDate) {
        _visibleDate.value = date
    }

    fun goToday() {
        val today = LocalDate.now()
        _selectedDate.value = today
        _visibleDate.value = today
    }

    fun onSwipe(direction: CalendarDateMode, isMonth: Boolean) {
        if (_mode.value == CalendarMode.LIST) return
        val delta = when (direction) {
            CalendarDateMode.FORWARD -> 1L
            CalendarDateMode.BACKWARD -> -1L
        }
        _visibleDate.value = shiftCalendarDate(_visibleDate.value, _mode.value, delta)
    }

    fun onTimelineScroll(firstVisibleDate: LocalDate) {
        if (_mode.value != CalendarMode.TIMELINE) return
        _visibleDate.value = firstVisibleDate
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

    private fun observeModeAndVisibleDate() {
        viewModelScope.launch {
            combine(_mode, _visibleDate) { mode, date -> mode to date }
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
            _visibleDate
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
            markedDatesStore.marked.collect { marks -> _marks.value = marks }
        }
    }

    private fun reload() {
        when (_mode.value) {
            CalendarMode.TIMELINE -> observeTimeline()
            else -> observeDays(_visibleDate.value, _mode.value)
        }
    }

    private fun observeDays(date: LocalDate, mode: CalendarMode) {
        daysJob?.cancel()
        daysJob = viewModelScope.launch {
            val range = calendarRange(date, mode, weekFields) ?: return@launch
            calendarData.observeRangeDays(range.start, range.endInclusive)
                .catch { t -> _events.tryEmit(CalendarUiEvent.Error(t.message ?: "Load error")) }
                .collect { days -> _days.value = days }
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
                .collect { days -> _days.value = days }
        }
    }

    private fun persistState() {
        viewModelScope.launch {
            _selectedDate
                .collect { savedStateHandle[keyDate] = it.toString() }
        }
        viewModelScope.launch {
            _mode
                .collect { savedStateHandle[keyMode] = it.name }
        }
    }

    companion object {
        private const val RANGE_YEARS_BACK = 1L
        private const val RANGE_YEARS_FORWARD = 2L
    }
}