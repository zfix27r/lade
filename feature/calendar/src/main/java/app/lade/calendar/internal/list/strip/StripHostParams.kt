package app.lade.calendar.internal.list.strip

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import app.lade.calendar.api.config.StripConfig
import app.lade.calendardata.api.DayProgress
import java.time.LocalDate

internal data class StripHostParams(
    val calendarDate: LocalDate,
    val calendarMarkedDates: State<Map<LocalDate, DayProgress>>,
    val config: StripConfig,
    val listState: LazyListState,
    val onDateSelected: (LocalDate) -> Unit,
    val modifier: Modifier,
)