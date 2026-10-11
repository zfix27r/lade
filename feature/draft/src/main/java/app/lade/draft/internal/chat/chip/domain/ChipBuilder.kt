package app.lade.draft.internal.chat.chip.domain

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.ui.graphics.vector.ImageVector
import app.lade.draft.internal.chat.chip.ChipKind
import app.lade.draftdata.DraftModel
import app.lade.entry.EntryKind
import app.lade.entry.ui.icon
import app.lade.humanize.api.Humanize
import app.lade.ui.extension.capitalize

internal class ChipBuilder(
    private val humanize: Humanize,
) {

    fun build(model: DraftModel): List<ChipData> = buildList {
        buildTitle(model)
        buildDate(model)
        buildTime(model)
        buildDuration(model)
        buildRrule(model)
        buildGoals(model)
        buildAlarms(model)
    }

    private fun MutableList<ChipData>.buildTitle(model: DraftModel) {
        model.title.takeIf { it.isNotBlank() }?.let {
            add(data(
                icon = (model.entryKind ?: EntryKind.NOTE).icon(),
                value = it,
                kind = ChipKind.TITLE,
                index = 0,
            ))
        }
    }

    private fun MutableList<ChipData>.buildDate(model: DraftModel) {
        val from = model.dateFrom
        val to = model.dateTo
        val value = when {
            from != null && to != null -> humanize.dateRange(from, to).best
            from != null -> humanize.date(from).best
            to != null -> humanize.date(to).best
            else -> return
        }
        add(data(
            icon = Icons.Outlined.CalendarToday,
            value = value.capitalize(),
            kind = ChipKind.DATE,
            index = 0,
        ))
    }

    private fun MutableList<ChipData>.buildTime(model: DraftModel) {
        val from = model.timeFrom
        val end = model.timeEnd
        val value = when {
            from != null && end != null -> humanize.timeRange(from, end).best
            from != null -> humanize.time(from).best
            end != null -> humanize.time(end).best
            else -> return
        }
        add(data(
            icon = Icons.Outlined.Schedule,
            value = value.capitalize(),
            kind = ChipKind.TIME,
            index = 0,
        ))
    }

    private fun MutableList<ChipData>.buildDuration(model: DraftModel) {
        val minutes = model.durationMinutes ?: return
        val h = minutes / 60
        val m = minutes % 60
        val value = when {
            h == 0 -> "${m}мин"
            m == 0 -> "${h}ч"
            else -> "${h}ч ${m}мин"
        }
        add(data(
            icon = Icons.Outlined.Timer,
            value = value,
            kind = ChipKind.DURATION,
            index = 0,
        ))
    }

    private fun MutableList<ChipData>.buildRrule(model: DraftModel) {
        model.rrule?.takeIf { it.isNotBlank() }?.let { rrule ->
            add(data(
                icon = Icons.Outlined.Repeat,
                value = humanize.rrule(rrule).best.capitalize(),
                kind = ChipKind.RRULE,
                index = 0,
            ))
        }
    }

    private fun MutableList<ChipData>.buildGoals(model: DraftModel) {
        model.goals.forEachIndexed { index, goal ->
            add(data(
                icon = Icons.Outlined.EmojiEvents,
                value = humanize.goal(
                    goal.title,
                    goal.unit,
                    goal.amount,
                    goal.repeat,
                    goal.weight,
                ).best.capitalize(),
                kind = ChipKind.GOAL,
                index = index,
            ))
        }
    }

    private fun MutableList<ChipData>.buildAlarms(model: DraftModel) {
        model.alarms.forEachIndexed { index, alarm ->
            add(data(
                icon = Icons.Outlined.Alarm,
                value = humanize.time(alarm.time).best.capitalize(),
                kind = ChipKind.ALARM,
                index = index,
            ))
        }
    }

    private fun data(
        icon: ImageVector,
        value: String,
        kind: ChipKind,
        index: Int,
    ): ChipData = ChipData(
        key = ChipKey(kind, index),
        icon = icon,
        value = value,
    )
}