package app.lade.entry.domain.models

data class EntryGoalDef(
    val key: String,
    val label: String? = null,
    val unit: String? = null,
    val target: Double? = null,
)