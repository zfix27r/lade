package app.lade.calendar.ui

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lade.calendar.data.MarkedDatesStore
import app.lade.calendar.domain.CalendarDateMode
import app.lade.calendar.domain.CalendarMode
import app.lade.calendar.domain.CalendarStateModel
import app.lade.calendar.domain.TimelineDay
import app.lade.calendardata.api.CalendarCardModel
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.Locale
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val calendarData: CalendarDataApi,
    private val markedDatesStore: MarkedDatesStore,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val keyDate = "calendar.currentDate"
    private val keyMode = "calendar.mode"

    private val _state = MutableStateFlow(
        CalendarStateModel(
            currentDate = savedStateHandle.get<String>(keyDate)?.let(LocalDate::parse)
                ?: LocalDate.now(),
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

    private var timelineWindow = TimelineWindow(
        anchor = _state.value.currentDate,
        start = _state.value.currentDate.minusDays(TIMELINE_PAST_DAYS),
        end = _state.value.currentDate.plusDays(TIMELINE_FUTURE_DAYS),
    )

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

    fun onVisibleMonthChange(month: YearMonth) {
        _state.update { if (it.visibleMonth == month) it else it.copy(visibleMonth = month) }
    }

    fun goToday() {
        _state.update { it.copy(currentDate = LocalDate.now()) }
    }

    fun onTimelineScroll(firstVisibleDate: LocalDate) {
        val mode = _state.value.mode
        if (mode != CalendarMode.TIMELINE) return
        val window = timelineWindow
        val nearStart = firstVisibleDate <= window.start.plusDays(TIMELINE_LOAD_THRESHOLD_DAYS)
        val nearEnd = firstVisibleDate >= window.end.minusDays(TIMELINE_LOAD_THRESHOLD_DAYS)
        if (nearStart) {
            timelineWindow = window.copy(start = window.start.minusDays(TIMELINE_PAGE_DAYS))
            observeTimeline()
        }
        if (nearEnd) {
            timelineWindow = window.copy(end = window.end.plusDays(TIMELINE_PAGE_DAYS))
            observeTimeline()
        }
    }

    fun onSwipe(direction: CalendarDateMode, isMonth: Boolean) {
        val delta = when (direction) {
            CalendarDateMode.FORWARD -> 1L
            CalendarDateMode.BACKWARD -> -1L
        }
        _state.update { s ->
            val newDate = when (s.mode) {
                CalendarMode.LIST -> if (isMonth) {
                    val target = s.currentDate.withDayOfMonth(1).plusMonths(delta)
                    target.withDayOfMonth(
                        s.currentDate.dayOfMonth.coerceAtMost(target.lengthOfMonth())
                    )
                } else {
                    s.currentDate.plusWeeks(delta)
                }
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
                CalendarMode.TIMELINE -> s.currentDate
            }
            s.copy(currentDate = newDate)
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
                            timelineWindow = TimelineWindow(
                                anchor = date,
                                start = date.minusDays(TIMELINE_PAST_DAYS),
                                end = date.plusDays(TIMELINE_FUTURE_DAYS),
                            )
                            observeTimeline()
                        }
                        else -> observeEntries(date, mode)
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
                    markedDatesStore.ensureRange(
                        from = date.minusMonths(1),
                        to = date.plusMonths(1),
                    )
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
                    CalendarMode.WEEK -> {
                        val weekStart = date.with(weekFields.dayOfWeek(), 1L)
                        calendarData.observeRange(weekStart, weekStart.plusDays(6))
                    }
                    CalendarMode.MONTH -> {
                        val from = date.withDayOfMonth(1)
                        val to = from.plusMonths(1).minusDays(1)
                        calendarData.observeRange(from, to)
                    }
                    CalendarMode.YEAR -> {
                        val from = date.withDayOfYear(1)
                        val to = from.plusYears(1).minusDays(1)
                        calendarData.observeRange(from, to)
                    }
                    CalendarMode.TIMELINE -> return@launch
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

    private fun buildTimelineDays(
        start: LocalDate,
        end: LocalDate,
        cards: List<CalendarCardModel>,
    ): List<TimelineDay> {
        val byDate = cards.groupBy { it.date }
        val days = mutableListOf<TimelineDay>()
        var cursor = start
        while (!cursor.isAfter(end)) {
            days += TimelineDay(
                date = cursor,
                entries = byDate[cursor].orEmpty(),
            )
            cursor = cursor.plusDays(1)
        }
        return days
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

    private data class TimelineWindow(
        val anchor: LocalDate,
        val start: LocalDate,
        val end: LocalDate,
    )

    private companion object {
        const val TIMELINE_PAST_DAYS = 30L
        const val TIMELINE_FUTURE_DAYS = 60L
        const val TIMELINE_PAGE_DAYS = 30L
        const val TIMELINE_LOAD_THRESHOLD_DAYS = 10L
    }
}

sealed interface CalendarUiEvent {
    data class Error(val message: String) : CalendarUiEvent
}