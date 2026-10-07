package app.lade.calendar.internal.data

import app.lade.calendardata.api.CalendarDataApi
import app.lade.calendardata.api.DayProgress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MarkedDatesStore @Inject constructor(
    private val calendarData: CalendarDataApi,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _marked = MutableStateFlow<Map<LocalDate, DayProgress>>(emptyMap())
    val marked: StateFlow<Map<LocalDate, DayProgress>> = _marked.asStateFlow()

    private var loadedRange: MarkedRange? = null
    private var initialJob: Job? = null
    private var prefetchJob: Job? = null
    private var slideJob: Job? = null
    private var initialStarted = false

    /** Первая фаза — вызвать один раз при старте экрана. */
    fun start(anchor: LocalDate) {
        if (initialStarted) {
            setAnchor(anchor)
            return
        }
        initialStarted = true

        val monthRange = MarkedRange.month(anchor)
        loadedRange = monthRange

        initialJob = scope.launch {
            calendarData.observeMarkedDates(monthRange.from, monthRange.to)
                .catch { }
                .collect { dates ->
                    _marked.update { it + dates }
                }
        }

        prefetchJob = scope.launch {
            val prefetchRange = MarkedRange.aroundMonth(
                anchor = anchor,
                radius = MarkedRangeDefaults.INITIAL_RADIUS_MONTHS,
            )
            calendarData.observeMarkedDates(prefetchRange.from, prefetchRange.to)
                .catch { }
                .collect { dates ->
                    _marked.update { it + dates }
                }
            loadedRange = prefetchRange
        }
    }

    /** Последующие вызовы — при свайпах. */
    fun setAnchor(anchor: LocalDate) {
        if (!initialStarted) {
            start(anchor)
            return
        }

        val current = loadedRange ?: return
        if (current.contains(anchor, MarkedRangeDefaults.MARGIN_MONTHS)) return

        val nextRange = current
            .extendedToward(anchor, MarkedRangeDefaults.EXTRA_MONTHS)
            .trimAround(anchor, MarkedRangeDefaults.MAX_MONTHS)

        if (nextRange == current) return

        loadedRange = nextRange

        _marked.update { old ->
            old.filterKeys { !it.isBefore(nextRange.from) && !it.isAfter(nextRange.to) }
        }

        slideJob?.cancel()
        slideJob = scope.launch {
            calendarData.observeMarkedDates(nextRange.from, nextRange.to)
                .catch { }
                .collect { dates ->
                    _marked.update { it + dates }
                }
        }
    }
}