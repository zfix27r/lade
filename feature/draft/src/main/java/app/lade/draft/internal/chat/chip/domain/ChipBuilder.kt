package app.lade.draft.internal.chat.chip.domain

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Update
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
        buildDateFrom(model)
        buildDateTo(model)
        buildTimeFrom(model)
        buildTimeEnd(model)
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

    private fun MutableList<ChipData>.buildDateFrom(model: DraftModel) {
        model.dateFrom?.let { date ->
            add(data(
                icon = Icons.Outlined.CalendarToday,
                value = humanize.date(date).best.capitalize(),
                kind = ChipKind.DATE_FROM,
                index = 0,
            ))
        }
    }

    private fun MutableList<ChipData>.buildDateTo(model: DraftModel) {
        model.dateTo?.let { date ->
            add(data(
                icon = Icons.Outlined.CalendarToday,
                value = humanize.date(date).best.capitalize(),
                kind = ChipKind.DATE_TO,
                index = 0,
            ))
        }
    }

    private fun MutableList<ChipData>.buildTimeFrom(model: DraftModel) {
        model.timeFrom?.let { time ->
            add(data(
                icon = Icons.Outlined.Schedule,
                value = humanize.time(time).best.capitalize(),
                kind = ChipKind.TIME_FROM,
                index = 0,
            ))
        }
    }

    private fun MutableList<ChipData>.buildTimeEnd(model: DraftModel) {
        model.timeEnd?.let { time ->
            add(data(
                icon = Icons.Outlined.Update,
                value = humanize.time(time).best.capitalize(),
                kind = ChipKind.TIME_END,
                index = 0,
            ))
        }
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