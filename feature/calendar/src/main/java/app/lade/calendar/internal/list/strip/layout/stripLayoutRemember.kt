package app.lade.calendar.internal.list.strip.layout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import app.lade.calendar.api.config.StripConfig
import java.time.LocalDate

@Composable
internal fun stripLayoutRemember(
    weeks: List<List<LocalDate>>,
    activeDate: LocalDate,
    config: StripConfig,
): StripLayout {
    val activeWeekIndex = remember(weeks, activeDate) {
        weeks.indexOfFirst { it.contains(activeDate) }.coerceAtLeast(0)
    }
    return remember(weeks, activeWeekIndex, config) {
        StripLayout(
            activeWeekIndex = activeWeekIndex,
            totalRows = weeks.size,
            rowHeight = config.rowHeight,
        )
    }
}