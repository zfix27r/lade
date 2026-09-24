package app.lade.draft.internal.chat

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Update
import app.lade.draft.internal.chat.chip.BarChatChip
import app.lade.draft.internal.chat.chip.BarChipKind
import app.lade.draftdata.DraftModel
import app.lade.humanize.api.Humanize

internal class DraftChipsBuilder(
    private val humanize: Humanize,
) {

    fun build(
        model: DraftModel,
        onChipClick: (BarChipKind, Int) -> Unit,
        onChipRemove: (BarChipKind, Int) -> Unit,
    ): DraftChips {
        return DraftChips(
            field = buildField(model, onChipClick, onChipRemove),
            goal = buildGoals(model, onChipClick, onChipRemove),
        )
    }

    private fun buildField(
        model: DraftModel,
        onChipClick: (BarChipKind, Int) -> Unit,
        onChipRemove: (BarChipKind, Int) -> Unit,
    ): List<BarChatChip> {
        val list = mutableListOf<BarChatChip>()

        model.dateFrom?.let { date ->
            list += BarChatChip(
                icon = Icons.Outlined.CalendarToday,
                value = humanize.date(date).best.capitalize(),
                kind = BarChipKind.DATE_FROM,
                onClick = { onChipClick(BarChipKind.DATE_FROM, 0) },
                onRemove = { onChipRemove(BarChipKind.DATE_FROM, 0) },
            )
        }
        model.dateTo?.let { date ->
            list += BarChatChip(
                icon = Icons.Outlined.CalendarToday,
                value = humanize.date(date).best.capitalize(),
                kind = BarChipKind.DATE_TO,
                onClick = { onChipClick(BarChipKind.DATE_TO, 0) },
                onRemove = { onChipRemove(BarChipKind.DATE_TO, 0) },
            )
        }
        model.timeFrom?.let { time ->
            list += BarChatChip(
                icon = Icons.Outlined.Schedule,
                value = humanize.time(time).best.capitalize(),
                kind = BarChipKind.TIME_FROM,
                onClick = { onChipClick(BarChipKind.TIME_FROM, 0) },
                onRemove = { onChipRemove(BarChipKind.TIME_FROM, 0) },
            )
        }
        model.timeEnd?.let { time ->
            list += BarChatChip(
                icon = Icons.Outlined.Update,
                value = humanize.time(time).best.capitalize(),
                kind = BarChipKind.TIME_END,
                onClick = { onChipClick(BarChipKind.TIME_END, 0) },
                onRemove = { onChipRemove(BarChipKind.TIME_END, 0) },
            )
        }
        model.rrule?.takeIf { it.isNotBlank() }?.let { rrule ->
            list += BarChatChip(
                icon = Icons.Outlined.Repeat,
                value = humanize.rrule(rrule).best.capitalize(),
                kind = BarChipKind.RRULE,
                onClick = { onChipClick(BarChipKind.RRULE, 0) },
                onRemove = { onChipRemove(BarChipKind.RRULE, 0) },
            )
        }

        return list
    }

    private fun buildGoals(
        model: DraftModel,
        onChipClick: (BarChipKind, Int) -> Unit,
        onChipRemove: (BarChipKind, Int) -> Unit,
    ): List<BarChatChip> {
        return model.goals.mapIndexed { index, goal ->
            BarChatChip(
                icon = Icons.Outlined.EmojiEvents,
                value = humanize.goal(
                    goal.title,
                    goal.unit,
                    goal.amount,
                    goal.repeat,
                    goal.weight,
                ).best.capitalize(),
                kind = BarChipKind.GOAL,
                index = index,
                onClick = { onChipClick(BarChipKind.GOAL, index) },
                onRemove = { onChipRemove(BarChipKind.GOAL, index) },
            )
        }
    }

    private fun String.capitalize(): String =
        replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}