package app.lade.entrydetailsscreen.domain.mapper

import app.lade.agenda.api.agenda.AgendaModel
import app.lade.agenda.api.entry.EntryModel
import app.lade.agenda.api.goal.GoalModel
import app.lade.entrydetailsscreen.domain.model.EntryEditUiState
import app.lade.entrydetailsscreen.domain.model.GoalDraft
import app.lade.entrydetailsscreen.domain.resolver.EntryKindInput
import app.lade.entrydetailsscreen.domain.resolver.EntryKindResolver

class EntryEditMapper(
    private val resolver: EntryKindResolver,
) {

    fun toUiState(agenda: AgendaModel, isNew: Boolean): EntryEditUiState {
        val entry = agenda.entry
        val goals = agenda.goals.map { GoalDraft.from(it) }
        val temporal = TemporalMapper.toTemporal(entry)
        return EntryEditUiState(
            id = entry.id,
            isNew = isNew,
            isArchived = entry.isArchived,
            title = entry.title,
            temporal = temporal,
            goals = goals,
            goalsExplicitlyEnabled = goals.isNotEmpty(),
            resolvedKind = resolver.resolve(
                EntryKindInput(temporal, goals.any { !it.isEmpty }, goals.isNotEmpty()),
            ),
            kindOverride = null,
            createdAtEpochMs = entry.createdAtEpochMs,
            templateId = entry.templateId,
        )
    }

    fun resolveKind(state: EntryEditUiState): EntryKindInput =
        EntryKindInput(
            temporal = state.temporal,
            hasGoals = state.goals.any { !it.isEmpty },
            goalsExplicitlyEnabled = state.goalsExplicitlyEnabled,
        )

    fun toEntryModel(state: EntryEditUiState): EntryModel =
        TemporalMapper.toEntryModel(state)

    fun toGoalModels(
        state: EntryEditUiState,
        entryId: Long,
        base: List<GoalModel>,
    ): List<GoalModel> {
        val baseById = base.associateBy { it.id }
        return state.goals.mapNotNull { draft ->
            if (draft.isEmpty) return@mapNotNull null
            val original = baseById[draft.id]
            GoalModel(
                id = draft.id,
                entryId = entryId,
                templateId = original?.templateId,
                title = draft.title.trim(),
                unit = draft.unit.storage,
                amount = draft.amountText.replace(',', '.').toDoubleOrNull()?.toInt(),
                repeat = draft.repeatText.toIntOrNull(),
                weight = draft.weight,
                archivedAtEpochMs = draft.archivedAtEpochMs,
            )
        }
    }
}