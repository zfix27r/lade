package app.lade.agenda.api.series

import java.time.LocalTime

data class SeriesEditDraft(
    val title: String? = null,
    val startTime: LocalTime? = null,
    val endTime: LocalTime? = null,
    val templateId: Long? = null,
)