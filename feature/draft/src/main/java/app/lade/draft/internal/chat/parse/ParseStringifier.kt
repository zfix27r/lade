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
        val from = model.dateFrom
        val to = model.dateTo
        return when {
            from != null && to != null -> humanize.dateRange(from, to).best
            from != null -> humanize.date(from).best
            to != null -> humanize.date(to).best
            else -> ""
        }
    }

    fun stringifyTime(model: DraftModel): String {
        model.timeRaw?.takeIf { it.isNotBlank() }?.let { return it }
        val from = model.timeFrom
        val end = model.timeEnd
        return when {
            from != null && end != null -> humanize.timeRange(from, end).best
            from != null -> humanize.time(from).best
            end != null -> humanize.time(end).best
            else -> ""
        }
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

    fun stringifyDuration(model: DraftModel): String {
        val minutes = model.durationMinutes ?: return ""
        val h = minutes / 60
        val m = minutes % 60
        return when {
            h == 0 -> "${m}мин"
            m == 0 -> "${h}ч"
            else -> "${h}ч ${m}мин"
        }
    }

    fun stringify(kind: ChipKind, model: DraftModel, index: Int): String {
        return when (kind) {
            ChipKind.TITLE -> stringifyTitle(model)
            ChipKind.DATE -> stringifyDate(model)
            ChipKind.TIME -> stringifyTime(model)
            ChipKind.DURATION -> stringifyDuration(model)
            ChipKind.RRULE -> stringifyRrule(model)
            ChipKind.GOAL -> model.goals.getOrNull(index)?.let { stringifyGoal(it) }.orEmpty()
            ChipKind.ALARM -> model.alarms.getOrNull(index)?.let { stringifyAlarm(it) }.orEmpty()
            ChipKind.REMINDER -> model.reminders.getOrNull(index)?.let { stringifyReminder(it) }
                .orEmpty()
        }
    }
}