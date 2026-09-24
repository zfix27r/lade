package app.lade.agendaui.internal

import app.lade.agenda.api.entry.EntryModel
import app.lade.entrykind.EntryKind
import app.lade.humanize.api.Humanize
import java.time.LocalDate

data class HeaderState(
    val title: String,
    val kind: EntryKind,
    val dateText: String?,
    val timeText: String?,
    val rruleText: String?,
)

internal fun EntryModel.toHeaderState(
    humanize: Humanize,
    fallbackDate: LocalDate,
): HeaderState {
    val from: LocalDate = dateFrom ?: fallbackDate
    val to: LocalDate? = dateTo
    val dateText = if (to != null && to.isAfter(from)) {
        "${humanize.date(from).best} – ${humanize.date(to).best}"
    } else {
        humanize.date(from).best
    }

    val start: java.time.LocalTime? = startTime
    val end: java.time.LocalTime? = endTime
    val timeText = when {
        start != null && end != null -> "${humanize.time(start).best} – ${humanize.time(end).best}"
        start != null -> humanize.time(start).best
        else -> null
    }

    val rruleText = rrule?.takeIf { it.isNotBlank() }?.let { humanize.rrule(it).best }

    return HeaderState(
        title = title,
        kind = kind,
        dateText = dateText,
        timeText = timeText,
        rruleText = rruleText,
    )
}