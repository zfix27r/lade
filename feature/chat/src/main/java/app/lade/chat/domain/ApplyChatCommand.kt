package app.lade.chat.domain

import app.lade.agenda.api.AgendaApi
import app.lade.agenda.api.Result
import app.lade.agenda.api.entry.EntryModel
import app.lade.agenda.api.log.LogOrigin
import app.lade.agenda.api.log.LogSaveGoalModel
import app.lade.agenda.api.log.LogSaveModel
import app.lade.entrykind.EntryKind
import javax.inject.Inject
import javax.inject.Singleton

sealed class ApplyResult {
    data class Ok(val messageKey: String, val detail: String) : ApplyResult()
    data class Err(
        val messageKey: String,
        val createTitle: String? = null,
        val createKind: String? = null,
    ) : ApplyResult()
}

@Singleton
class ApplyChatCommand @Inject constructor(
    private val agendaApi: AgendaApi,
) {
    suspend fun apply(command: ChatCommand): ApplyResult = when (command) {
        is ChatCommand.MarkHistory -> applyMark(command)
        is ChatCommand.UpsertEntry -> applyUpsert(command)
        is ChatCommand.TimedBlock -> applyTimed(command)
        is ChatCommand.Create -> ApplyResult.Err(
            messageKey = "habit_missing",
            createTitle = command.title,
            createKind = command.kind.storage,
        )
    }

    private suspend fun applyMark(command: ChatCommand.MarkHistory): ApplyResult {
        val agenda = agendaApi.get(command.entryId, command.date)
            ?: return ApplyResult.Err("entry_missing")

        val goals = agenda.goals.map { goal ->
            LogSaveGoalModel(
                goalId = goal.id,
                amount = goal.amount,
                repeat = goal.repeat,
                weight = goal.weight,
            )
        }

        val saveModel = LogSaveModel(
            date = command.date,
            goals = goals,
            origin = LogOrigin.CHAT,
        )

        return when (agendaApi.saveLogs(saveModel)) {
            is Result.Success -> {
                val detail = buildString {
                    append(command.label)
                    command.actuals.forEach { a ->
                        append(" · ")
                        a.value?.let { append(it) }
                        a.unit?.let { append(' ').append(it) }
                        if (a.key != "value") append(" ").append(a.key)
                    }
                }
                ApplyResult.Ok("habit", detail)
            }

            is Result.Failure -> ApplyResult.Err("save_failed")
        }
    }

    private suspend fun applyUpsert(command: ChatCommand.UpsertEntry): ApplyResult {
        val base = command.entryId?.let { agendaApi.get(it, command.date)?.entry }
        val entry = (base ?: EntryModel(
            kind = command.kind,
            title = command.title,
            createdAtEpochMs = System.currentTimeMillis(),
        )).copy(
            title = command.title,
            kind = command.kind,
            dateFrom = command.date,
            dateTo = command.date,
            startTime = command.startTime,
            endTime = command.endTime,
            rrule = null,
        )

        return when (agendaApi.saveEntry(entry)) {
            is Result.Success -> {
                val detail = buildString {
                    append(command.title)
                    command.startTime?.let { start ->
                        append(' ').append(start)
                        command.endTime?.let { append('–').append(it) }
                    }
                }
                ApplyResult.Ok(
                    if (command.kind == EntryKind.TASK) "task" else "block",
                    detail,
                )
            }

            is Result.Failure -> ApplyResult.Err("save_failed")
        }
    }

    private suspend fun applyTimed(command: ChatCommand.TimedBlock): ApplyResult {
        val base = command.entryId?.let { agendaApi.get(it, command.date)?.entry }
        val entry = (base ?: EntryModel(
            kind = command.kind,
            title = command.title,
            createdAtEpochMs = System.currentTimeMillis(),
        )).copy(
            title = command.title,
            kind = command.kind,
            dateFrom = command.date,
            dateTo = command.date,
            startTime = command.startTime,
            endTime = command.endTime,
            rrule = null,
        )

        return when (agendaApi.saveEntry(entry)) {
            is Result.Success -> {
                val detail = buildString {
                    append(command.title)
                    append(' ').append(command.startTime)
                    append('–').append(command.endTime)
                }
                ApplyResult.Ok("block", detail)
            }

            is Result.Failure -> ApplyResult.Err("save_failed")
        }
    }
}