package app.lade.draft.internal.parse

import app.lade.chat.api.FieldKey
import app.lade.chat.api.FieldValue
import app.lade.chat.api.ParseResult
import app.lade.draftdata.DraftGoal
import app.lade.draftdata.DraftModel

internal fun ParseResult.toDraftModel(base: DraftModel): DraftModel {
    var draft = base

    fields.forEach { field ->
        when (field.key) {
            FieldKey.KIND -> {
                val value = field.value as? FieldValue.Kind ?: return@forEach
                draft = draft.copy(kind = value.value)
            }
            FieldKey.DATE_FROM -> {
                val value = field.value as? FieldValue.Date ?: return@forEach
                draft = draft.copy(dateFrom = value.value)
            }
            FieldKey.DATE_TO -> {
                val value = field.value as? FieldValue.Date ?: return@forEach
                draft = draft.copy(dateTo = value.value)
            }
            FieldKey.TIME_FROM -> {
                val value = field.value as? FieldValue.Time ?: return@forEach
                draft = draft.copy(timeFrom = value.value)
            }
            FieldKey.TIME_END -> {
                val value = field.value as? FieldValue.Time ?: return@forEach
                draft = draft.copy(timeEnd = value.value)
            }
            FieldKey.RRULE -> {
                val value = field.value as? FieldValue.Text ?: return@forEach
                draft = draft.copy(rrule = value.value)
            }
            FieldKey.GOAL -> Unit
            FieldKey.ALARM -> Unit
            FieldKey.REMINDER -> Unit
        }
    }

    if (goals.isNotEmpty()) {
        draft = draft.copy(goals = mergeGoals(draft.goals, goals))
    }

    return draft.copy(title = remaining.ifBlank { draft.title })
}

private fun mergeGoals(old: List<DraftGoal>, new: List<DraftGoal>): List<DraftGoal> {
    val newByTitle = new.filter { it.title.isNotBlank() }.associateBy { it.title }
    val kept = old.filter { it.title.isBlank() || it.title !in newByTitle }
    return kept + new
}