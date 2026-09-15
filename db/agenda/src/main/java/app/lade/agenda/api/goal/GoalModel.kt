package app.lade.agenda.api.goal

data class GoalModel(
    val id: Long = 0,
    val entryId: Long,
    val templateId: Long? = null,
    val title: String,
    val unit: String,
    val amount: Int? = null,
    val repeat: Int? = null,
    val weight: Double? = null,
    val archivedAtEpochMs: Long? = null,
)