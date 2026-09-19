package app.lade.entrydetailsscreen.domain.model

import app.lade.agenda.api.goal.GoalModel
import app.lade.agenda.api.goal.GoalUnit

data class GoalDraft(
    val id: Long = 0,
    val title: String = "",
    val unit: GoalUnit = GoalUnit.SET,
    val amountText: String = "",
    val repeatText: String = "",
    val weight: Double? = null,
    val archivedAtEpochMs: Long? = null,
) {
    val isEmpty: Boolean
        get() = title.isBlank() && amountText.isBlank() && repeatText.isBlank()

    companion object {
        fun from(model: GoalModel): GoalDraft = GoalDraft(
            id = model.id,
            title = model.title,
            unit = GoalUnit.fromStorage(model.unit),
            amountText = model.amount?.toString().orEmpty(),
            repeatText = model.repeat?.toString().orEmpty(),
            weight = model.weight,
            archivedAtEpochMs = model.archivedAtEpochMs,
        )
    }
}