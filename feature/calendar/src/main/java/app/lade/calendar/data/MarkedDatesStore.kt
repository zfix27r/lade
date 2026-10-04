package app.lade.calendar.data

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

    private var coveredFrom: LocalDate? = null
    private var coveredTo: LocalDate? = null
    private var job: Job? = null

    fun ensureRange(from: LocalDate, to: LocalDate) {
        val cf = coveredFrom
        val ct = coveredTo
        if (cf != null && ct != null && !from.isBefore(cf) && !to.isAfter(ct)) return

        val newFrom = if (cf == null) from else minOf(cf, from)
        val newTo = if (ct == null) to else maxOf(ct, to)

        coveredFrom = newFrom
        coveredTo = newTo

        job?.cancel()
        job = scope.launch {
            calendarData.observeMarkedDates(newFrom, newTo)
                .catch { }
                .collect { dates ->
                    _marked.value = dates
                }
        }
    }
}