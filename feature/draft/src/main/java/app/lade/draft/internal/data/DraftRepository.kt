package app.lade.draft.internal.data

import app.lade.agenda.api.AgendaApi
import app.lade.agenda.api.Result
import app.lade.agenda.api.agenda.AgendaError
import app.lade.agenda.api.agenda.AgendaSaveModel
import app.lade.draftdata.DraftModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DraftRepository @Inject constructor(
    private val agendaApi: AgendaApi,
) {

    suspend fun load(entryId: Long): DraftModel? {
        val agenda = agendaApi.get(entryId, null) ?: return null
        return agenda.toDraft()
    }

    suspend fun save(draft: DraftModel): Result<Long, AgendaError> {
        val existing = draft.entryId?.let { agendaApi.get(it, null)?.entry }
        val entry = draft.toEntryModel(existing)
        val saveModel = AgendaSaveModel(
            entry = entry,
            goals = draft.goals.map { it.toGoalModel(entry.id) },
        )
        return agendaApi.saveAgenda(saveModel)
    }
}