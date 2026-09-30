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

    fun buildEntryChips(
        model: DraftModel,
        states: ChipStates,
        onChipClick: (ChipKind, Int) -> Unit,
        onChipRemove: (ChipKind, Int) -> Unit,
    ): List<ChipModel> = buildList {
        model.title.takeIf { it.isNotBlank() }?.let {
            add(
                chip(
                    icon = (model.entryKind ?: EntryKind.NOTE).icon(),
                    value = it,
                    kind = ChipKind.TITLE,
                    index = 0,
                    states = states,
                    onChipClick = onChipClick,
                    onChipRemove = onChipRemove,
                )
            )
        }
        model.dateFrom?.let { date ->
            add(
                chip(
                    icon = Icons.Outlined.CalendarToday,
                    value = humanize.date(date).best.capitalize(),
                    kind = ChipKind.DATE_FROM,
                    index = 0,
                    states = states,
                    onChipClick = onChipClick,
                    onChipRemove = onChipRemove,
                )
            )
        }
        model.dateTo?.let { date ->
            add(
                chip(
                    icon = Icons.Outlined.CalendarToday,
                    value = humanize.date(date).best.capitalize(),
                    kind = ChipKind.DATE_TO,
                    index = 0,
                    states = states,
                    onChipClick = onChipClick,
                    onChipRemove = onChipRemove,
                )
            )
        }
        model.timeFrom?.let { time ->
            add(
                chip(
                    icon = Icons.Outlined.Schedule,
                    value = humanize.time(time).best.capitalize(),
                    kind = ChipKind.TIME_FROM,
                    index = 0,
                    states = states,
                    onChipClick = onChipClick,
                    onChipRemove = onChipRemove,
                )
            )
        }
        model.timeEnd?.let { time ->
            add(
                chip(
                    icon = Icons.Outlined.Update,
                    value = humanize.time(time).best.capitalize(),
                    kind = ChipKind.TIME_END,
                    index = 0,
                    states = states,
                    onChipClick = onChipClick,
                    onChipRemove = onChipRemove,
                )
            )
        }
        model.rrule?.takeIf { it.isNotBlank() }?.let { rrule ->
            add(
                chip(
                    icon = Icons.Outlined.Repeat,
                    value = humanize.rrule(rrule).best.capitalize(),
                    kind = ChipKind.RRULE,
                    index = 0,
                    states = states,
                    onChipClick = onChipClick,
                    onChipRemove = onChipRemove,
                )
            )
        }
    }

    fun buildGoalChips(
        model: DraftModel,
        states: ChipStates,
        onChipClick: (ChipKind, Int) -> Unit,
        onChipRemove: (ChipKind, Int) -> Unit,
    ): List<ChipModel> = model.goals.mapIndexed { index, goal ->
        chip(
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
            states = states,
            onChipClick = onChipClick,
            onChipRemove = onChipRemove,
        )
    }

    fun buildAlarmChips(
        model: DraftModel,
        states: ChipStates,
        onChipClick: (ChipKind, Int) -> Unit,
        onChipRemove: (ChipKind, Int) -> Unit,
    ): List<ChipModel> = model.alarms.mapIndexed { index, alarm ->
        chip(
            icon = Icons.Outlined.Alarm,
            value = humanize.time(alarm.time).best.capitalize(),
            kind = ChipKind.ALARM,
            index = index,
            states = states,
            onChipClick = onChipClick,
            onChipRemove = onChipRemove,
        )
    }

    private fun chip(
        icon: ImageVector,
        value: String,
        kind: ChipKind,
        index: Int,
        states: ChipStates,
        onChipClick: (ChipKind, Int) -> Unit,
        onChipRemove: (ChipKind, Int) -> Unit,
    ): ChipModel {
        val key = ChipKey(kind, index)
        val state = states.stateOf(key)
        val emphasis = states.emphasisOf(key)
        return ChipModel(
            icon = icon,
            value = value,
            kind = kind,
            index = index,
            state = state,
            emphasis = emphasis,
            onClick = { onChipClick(kind, index) },
            onRemove = { onChipRemove(kind, index) },
        )
    }
}