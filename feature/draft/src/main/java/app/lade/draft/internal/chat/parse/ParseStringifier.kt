package app.lade.draft.internal.chat.parse

import app.lade.draft.internal.chat.chip.ChipKind
import app.lade.draftdata.DraftAlarm
import app.lade.draftdata.DraftGoal
import app.lade.draftdata.DraftModel
import app.lade.draftdata.DraftReminder
import app.lade.humanize.api.Humanize

internal class ParseStringifier(
    private val humanize: Humanize,
) {

    fun stringifyTitle(model: DraftModel): String {
        model.titleRaw?.takeIf { it.isNotBlank() }?.let { return it }
        return model.title
    }

    fun stringifyDate(model: DraftModel): String {
        model.dateRaw?.takeIf { it.isNotBlank() }?.let { return it }
        return buildString {
            model.dateFrom?.let { append(humanize.date(it).best) }
            model.dateTo?.let {
                if (isNotEmpty()) append(" — ")
                append(humanize.date(it).best)
            }
        }.trim()
    }

    fun stringifyTime(model: DraftModel): String {
        model.timeRaw?.takeIf { it.isNotBlank() }?.let { return it }
        return buildString {
            model.timeFrom?.let { append(humanize.time(it).best) }
            model.timeEnd?.let {
                if (isNotEmpty()) append(" — ")
                append(humanize.time(it).best)
            }
        }.trim()
    }

    fun stringifyRrule(model: DraftModel): String {
        model.rruleRaw?.takeIf { it.isNotBlank() }?.let { return it }
        return model.rrule?.takeIf { it.isNotBlank() }?.let { humanize.rrule(it).best }.orEmpty()
    }

    fun stringifyGoal(goal: DraftGoal): String {
        goal.raw?.takeIf { it.isNotBlank() }?.let { return it }

        return buildString {
            if (goal.title.isNotBlank()) append(goal.title)
            goal.amount?.let {
                if (isNotEmpty()) append(" ")
                append(it)
            }
            goal.unit.takeIf { it.name != "UNKNOWN" }?.let {
                if (isNotEmpty()) append(" ")
                append(it.name.lowercase())
            }
            goal.repeat?.let {
                if (isNotEmpty()) append(" ")
                append("по ")
                append(it)
            }
            goal.weight?.let {
                if (isNotEmpty()) append(" ")
                append(it)
                append("кг")
            }
        }.trim()
    }

    fun stringifyAlarm(alarm: DraftAlarm): String {
        alarm.raw?.takeIf { it.isNotBlank() }?.let { return it }
        return humanize.time(alarm.time).best
    }

    fun stringifyReminder(reminder: DraftReminder): String {
        reminder.raw?.takeIf { it.isNotBlank() }?.let { return it }
        return "за ${reminder.minutesBefore} мин"
    }

    fun stringify(kind: ChipKind, model: DraftModel, index: Int): String {
        return when (kind) {
            ChipKind.TITLE -> stringifyTitle(model)
            ChipKind.DATE_FROM, ChipKind.DATE_TO -> stringifyDate(model)
            ChipKind.TIME_FROM, ChipKind.TIME_END -> stringifyTime(model)
            ChipKind.RRULE -> stringifyRrule(model)
            ChipKind.GOAL -> model.goals.getOrNull(index)?.let { stringifyGoal(it) }.orEmpty()
            ChipKind.ALARM -> model.alarms.getOrNull(index)?.let { stringifyAlarm(it) }.orEmpty()
            ChipKind.REMINDER -> model.reminders.getOrNull(index)?.let { stringifyReminder(it) }.orEmpty()
        }
    }
}