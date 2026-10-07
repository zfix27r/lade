package app.lade.calendar.internal.data

import app.lade.calendar.api.config.TimelineWindowConfig
import java.time.LocalDate

internal data class TimelineWindow(
    val anchor: LocalDate,
    val start: LocalDate,
    val end: LocalDate,
) {
    fun growsStart(page: Long): TimelineWindow =
        copy(start = start.minusDays(page))

    fun growsEnd(page: Long): TimelineWindow =
        copy(end = end.plusDays(page))

    fun isNearStart(firstVisible: LocalDate, threshold: Long): Boolean =
        firstVisible <= start.plusDays(threshold)

    fun isNearEnd(firstVisible: LocalDate, threshold: Long): Boolean =
        firstVisible >= end.minusDays(threshold)
}

internal fun timelineWindow(
    anchor: LocalDate,
    config: TimelineWindowConfig,
): TimelineWindow = TimelineWindow(
    anchor = anchor,
    start = anchor.minusDays(config.pastDays),
    end = anchor.plusDays(config.futureDays),
)