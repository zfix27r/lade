package app.lade.calendar.internal

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lade.calendar.api.config.DefaultTimelineWindowConfig
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
import app.lade.calendardata.api.CalendarDataApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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
import kotlin.time.Duration.Companion.milliseconds

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

    fun loadGoalDetails(entryId: Long, date: LocalDate) {
        val key = expandableKey(entryId, date)
        if (_state.value.goalDetailsCache.containsKey(key)) return
        viewModelScope.launch {
            runCatching {
                calendarData.getGoalDetails(entryId, date)
            }.onSuccess { details ->
                _state.update {
                    it.copy(goalDetailsCache = it.goalDetailsCache + (key to details))
                }
            }.onFailure {
                _events.tryEmit(CalendarUiEvent.Error("Failed to load goal details"))
            }
        }
    }

    fun setGoalAmount(entryId: Long, date: LocalDate, goalId: Long, amount: Int) {
        viewModelScope.launch {
            runCatching {
                calendarData.setGoalAmount(entryId, date, goalId, amount)
            }.onSuccess {
                val key = expandableKey(entryId, date)
                _state.update { state ->
                    val cached = state.goalDetailsCache[key] ?: return@update state
                    val updated = cached.map { goal ->
                        if (goal.id == goalId) {
                            goal.copy(
                                actualAmount = amount,
                                isDone = goal.plannedAmount?.let { amount >= it } ?: goal.isDone,
                            )
                        } else {
                            goal
                        }
                    }
                    state.copy(goalDetailsCache = state.goalDetailsCache + (key to updated))
                }
            }.onFailure {
                _events.tryEmit(CalendarUiEvent.Error("Failed to set goal amount"))
            }
        }
    }

    private var saveGoalJob: Job? = null

    fun setGoalValue(
        entryId: Long,
        date: LocalDate,
        goalId: Long,
        value: Int,
    ) {
        val key = expandableKey(entryId, date)
        val cached = _state.value.goalDetailsCache[key]
        val goal = cached?.firstOrNull { it.id == goalId } ?: return
        val planned = goal.plannedAmount ?: return
        val clamped = value.coerceIn(0, planned)

        _state.update { state ->
            val list = state.goalDetailsCache[key] ?: return@update state
            val updated = list.map { g ->
                if (g.id == goalId) {
                    g.copy(
                        actualAmount = clamped,
                        isDone = clamped >= planned,
                    )
                } else {
                    g
                }
            }
            state.copy(goalDetailsCache = state.goalDetailsCache + (key to updated))
        }

        saveGoalJob?.cancel()
        saveGoalJob = viewModelScope.launch {
            delay(SAVE_GOAL_DEBOUNCE_MS.milliseconds)
            runCatching {
                calendarData.setGoalAmount(entryId, date, goalId, clamped)
            }.onFailure {
                _events.tryEmit(CalendarUiEvent.Error("Failed to save goal"))
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

    private companion object {
        const val SAVE_GOAL_DEBOUNCE_MS = 400L
    }

    private fun expandableKey(entryId: Long, date: LocalDate): String =
        "expandable-$entryId-${date.toEpochDay()}"

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
}