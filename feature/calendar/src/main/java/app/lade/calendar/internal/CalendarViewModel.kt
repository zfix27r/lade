package app.lade.calendar.internal

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lade.calendar.api.config.DefaultStripConfig
import app.lade.calendar.api.config.DefaultTimelineWindowConfig
import app.lade.calendar.api.config.StripConfig
import app.lade.calendar.api.config.TimelineWindowConfig
import app.lade.calendar.internal.data.MarkedDatesStore
import app.lade.calendar.internal.data.buildTimelineDays
import app.lade.calendar.internal.data.calendarRange
import app.lade.calendar.internal.data.shiftCalendarDate
import app.lade.calendar.internal.data.timelineWindow
import app.lade.calendar.internal.domain.CalendarDateMode
import app.lade.calendar.internal.domain.CalendarStateModel
import app.lade.calendar.internal.domain.CalendarUiEvent
import app.lade.calendar.internal.domain.mode.CalendarMode
import app.lade.calendar.internal.list.strip.data.StripStateHolder
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
    private val markedDatesStore: MarkedDatesStore,
    private val savedStateHandle: SavedStateHandle,
    private val timelineConfig: TimelineWindowConfig = DefaultTimelineWindowConfig,
) : ViewModel() {

    private val keyDate = "calendar.currentDate"
    private val keyMode = "calendar.mode"

    private val initialDate = savedStateHandle.get<String>(keyDate)?.let(LocalDate::parse)
        ?: LocalDate.now()

    private val _state = MutableStateFlow(
        CalendarStateModel(
            currentDate = initialDate,
            mode = savedStateHandle.get<String>(keyMode)
                ?.let { runCatching { CalendarMode.valueOf(it) }.getOrNull() }
                ?: CalendarMode.LIST,
        )
    )
    val state: StateFlow<CalendarStateModel> = _state.asStateFlow()

    private val _events = MutableSharedFlow<CalendarUiEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<CalendarUiEvent> = _events.asSharedFlow()

    private val weekFields: WeekFields = WeekFields.of(Locale.getDefault())

    private var entriesJob: Job? = null
    private var timelineJob: Job? = null

    private var timelineWindow = timelineWindow(_state.value.currentDate, timelineConfig)

    val strip = StripStateHolder(initialDate = _state.value.currentDate)

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
        strip.setAnchor(date)
    }

    fun goToday() {
        val today = LocalDate.now()
        _state.update { it.copy(currentDate = today) }
        strip.setAnchor(today)
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

                        else -> observeEntries(date, mode)
                    }
                }
        }
    }

    private fun observeMarkedDates() {
        viewModelScope.launch {
            strip.state
                .map { s -> MarkedKey(s.date, s.isMonthMode) }
                .distinctUntilChanged()
                .collect { key ->
                    if (!markedDatesStoreStarted) {
                        markedDatesStore.start(key.anchor)
                        markedDatesStoreStarted = true
                    } else {
                        markedDatesStore.setAnchor(key.anchor)
                    }
                }
        }
        viewModelScope.launch {
            markedDatesStore.marked.collect { dates ->
                _state.update { it.copy(markedDates = dates) }
            }
        }
    }

    private fun reload() {
        val s = _state.value
        when (s.mode) {
            CalendarMode.TIMELINE -> observeTimeline()
            else -> observeEntries(s.currentDate, s.mode)
        }
    }

    private fun observeEntries(date: LocalDate, mode: CalendarMode) {
        entriesJob?.cancel()
        entriesJob = viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val flow = when (mode) {
                    CalendarMode.LIST -> calendarData.observeList(date)
                    CalendarMode.TIMELINE -> return@launch
                    else -> {
                        val range = calendarRange(date, mode, weekFields) ?: return@launch
                        calendarData.observeRange(range.start, range.endInclusive)
                    }
                }
                flow
                    .catch { t ->
                        _state.update {
                            it.copy(isLoading = false, error = t.message ?: "Load error")
                        }
                    }
                    .collect { cards ->
                        _state.update { it.copy(entries = cards, isLoading = false) }
                    }
            } catch (t: Throwable) {
                _state.update {
                    it.copy(isLoading = false, error = t.message ?: "Load error")
                }
            }
        }
    }

    private fun observeTimeline() {
        timelineJob?.cancel()
        timelineJob = viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val window = timelineWindow
            try {
                calendarData.observeRange(window.start, window.end)
                    .catch { t ->
                        _state.update {
                            it.copy(isLoading = false, error = t.message ?: "Load error")
                        }
                    }
                    .collect { cards ->
                        _state.update {
                            it.copy(
                                timelineDays = buildTimelineDays(
                                    start = window.start,
                                    end = window.end,
                                    cards = cards,
                                ),
                                isLoading = false,
                            )
                        }
                    }
            } catch (t: Throwable) {
                _state.update {
                    it.copy(isLoading = false, error = t.message ?: "Load error")
                }
            }
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

    private data class MarkedKey(
        val anchor: LocalDate,
        val isMonth: Boolean,
    )
}