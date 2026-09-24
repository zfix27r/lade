package app.lade.agendaui.internal

import app.lade.agenda.api.agenda.AgendaModel
import app.lade.agenda.api.goal.GoalModel
import app.lade.agenda.api.goal.GoalUnit
import app.lade.agenda.api.log.LogModel
import app.lade.humanize.api.Humanize

data class GoalCardState(
    val goalId: Long,
    val title: String,
    val targetText: String,
    val factText: String,
    val progress: Float,
    val isDone: Boolean,
)

internal fun buildGoalCards(
    agenda: AgendaModel,
    humanize: Humanize,
): List<GoalCardState> {
    val logsByGoal = agenda.logs
        .filter { it.goalId != null }
        .associateBy { it.goalId }

    return agenda.goals.map { goal ->
        goal.toCardState(logsByGoal[goal.id], humanize)
    }
}

private fun GoalModel.toCardState(
    log: LogModel?,
    humanize: Humanize,
): GoalCardState {
    val unit = GoalUnit.fromStorage(unit)
    val targetText = humanize.goal(
        title = "",
        unit = unit,
        amount = amount,
        repeat = repeat,
        weight = weight,
    ).best

    val targetTotal = (amount ?: 0) * (repeat ?: 1)
    val factTotal = (log?.actualAmount ?: 0) * (log?.actualRepeat ?: 1)
    val progress = if (targetTotal > 0) {
        (factTotal.toFloat() / targetTotal).coerceIn(0f, 1f)
    } else {
        if (factTotal > 0) 1f else 0f
    }
    val isDone = targetTotal > 0 && factTotal >= targetTotal

    val factText = if (log == null) {
        "—"
    } else {
        val (a, r) = splitFact(factTotal, amount, repeat)
        humanize.goal(
            title = "",
            unit = unit,
            amount = a,
            repeat = r,
            weight = log.actualWeight,
        ).best
    }

    return GoalCardState(
        goalId = id,
        title = title,
        targetText = targetText,
        factText = factText,
        progress = progress,
        isDone = isDone,
    )
}