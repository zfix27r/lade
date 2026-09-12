package app.lade.entry.domain.models

import java.time.LocalTime

data class SeriesEditDraft(
    val title: String? = null,
    val startTime: LocalTime? = null,
    val endTime: LocalTime? = null,
    val categoryId: Long? = null,
)