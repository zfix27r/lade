package app.lade.draft.internal.chat.chip

import app.lade.parser.api.ParserContract
import app.lade.parser.api.ParserEntryModel
import app.lade.parser.api.ParserGoalModel
import app.lade.parser.api.ParserModel
import app.lade.draft.internal.chat.chip.domain.ChipKey
import app.lade.draftdata.DraftGoal
import app.lade.draftdata.DraftModel
import app.lade.goal.GoalUnit
import java.time.LocalDate
import java.time.LocalTime

internal object ChipRules {

    fun collectKeys(model: DraftModel): Set<ChipKey> = buildSet {
        if (model.title.isNotBlank()) add(ChipKey(ChipKind.TITLE, 0))
        if (model.dateFrom != null || model.dateTo != null) add(ChipKey(ChipKind.DATE, 0))
        if (model.timeFrom != null || model.timeEnd != null) {
            add(ChipKey(ChipKind.TIME, 0))
        }
        if (model.durationMinutes != null) {
            add(ChipKey(ChipKind.DURATION, 0))
        }
        if (!model.rrule.isNullOrBlank()) add(ChipKey(ChipKind.RRULE, 0))
        model.goals.forEachIndexed { i, _ -> add(ChipKey(ChipKind.GOAL, i)) }
        model.alarms.forEachIndexed { i, _ -> add(ChipKey(ChipKind.ALARM, i)) }
        model.reminders.forEachIndexed { i, _ -> add(ChipKey(ChipKind.REMINDER, i)) }
    }

    fun remove(model: DraftModel, key: ChipKey): DraftModel = when (key.kind) {
        ChipKind.TITLE -> model.copy(title = "")
        ChipKind.DATE -> model.copy(dateFrom = null, dateTo = null)
        ChipKind.TIME -> model.copy(timeFrom = null, timeEnd = null)
        ChipKind.DURATION -> model.copy(durationMinutes = null)
        ChipKind.RRULE -> model.copy(rrule = null)
        ChipKind.GOAL -> model.copy(goals = model.goals.filterIndexed { i, _ -> i != key.index })
        ChipKind.ALARM -> model.copy(alarms = model.alarms.filterIndexed { i, _ -> i != key.index })
        ChipKind.REMINDER -> model.copy(reminders = model.reminders.filterIndexed { i, _ -> i != key.index })
    }

    fun merge(base: DraftModel, result: ParserModel): DraftModel {
        var draft = base
        val entry = result.entry
        if (entry != null) {
            draft = draft.copy(
                title = entry.mergeTitle(base.title),
                dateFrom = entry.mergeDate(base.dateFrom, entry.dateFrom),
                dateTo = entry.mergeDate(base.dateTo, entry.dateTo),
                timeFrom = entry.mergeTime(base.timeFrom, entry.timeFrom),
                timeEnd = entry.mergeTime(base.timeEnd, entry.timeTo),
                durationMinutes = entry.mergeDuration(base.durationMinutes),
                rrule = entry.mergeRrule(base.rrule),
            )
        }
        val goals = result.goals
        if (!goals.isNullOrEmpty()) {
            draft = draft.copy(goals = mergeGoals(draft.goals, goals.map { it.toDraftGoal() }))
        }
        return draft
    }

    private fun mergeGoals(existing: List<DraftGoal>, incoming: List<DraftGoal>): List<DraftGoal> {
        val result = existing.toMutableList()
        incoming.forEach { goal ->
            val index = result.indexOfFirst { it.title.equals(goal.title, ignoreCase = true) }
            if (index >= 0) result[index] = goal else result.add(goal)
        }
        return result
    }
}

private fun ParserEntryModel.mergeTitle(base: String): String =
    when {
        ParserContract.isSkip(title) -> base
        ParserContract.isFind(title) -> base
        else -> title ?: base
    }

private fun ParserEntryModel.mergeDate(base: LocalDate?, value: String?): LocalDate? =
    when {
        ParserContract.isSkip(value) -> base
        ParserContract.isFind(value) -> base
        value != null -> LocalDate.parse(value)
        else -> base
    }

private fun ParserEntryModel.mergeTime(base: LocalTime?, value: String?): LocalTime? =
    when {
        ParserContract.isSkip(value) -> base
        ParserContract.isFind(value) -> base
        value != null -> LocalTime.parse(value)
        else -> base
    }

private fun ParserEntryModel.mergeRrule(base: String?): String? =
    when {
        ParserContract.isSkip(rrule) -> base
        ParserContract.isFind(rrule) -> base
        else -> rrule
    }

private fun ParserEntryModel.mergeDuration(base: Int?): Int? {
    val value = durationMinutes
    return when {
        ParserContract.isSkip(value) -> base
        ParserContract.isFind(value) -> base
        value != null -> value.toIntOrNull() ?: base
        else -> base
    }
}

private fun ParserGoalModel.toDraftGoal(): DraftGoal = DraftGoal(
    title = title.orEmpty(),
    amount = amount?.toIntOrNull(),
    unit = GoalUnit.fromStorage(unit),
    repeat = repeats?.toIntOrNull(),
    weight = weight?.toDoubleOrNull(),
)