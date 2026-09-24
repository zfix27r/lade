package app.lade.agendaui.internal

import app.lade.agenda.api.agenda.AgendaModel
import app.lade.agenda.api.goal.GoalUnit
import app.lade.humanize.api.Humanize

data class GoalMarkState(
    val goalId: Long,
    val title: String,
    val targetText: String,
    val targetTotal: Int,
    val currentTotal: Int,
    val averageTotal: Int,
    val unit: GoalUnit,
    val amount: Int?,
    val repeat: Int?,
    val weight: Double?,
) {
    val currentAmount: Int get() = splitFact(currentTotal, amount, repeat).first
    val currentRepeat: Int get() = splitFact(currentTotal, amount, repeat).second

    val currentText: String get() = buildString {
        if (repeat != null && repeat > 0 && unit == GoalUnit.REP) {
            append(currentAmount).append("х").append(currentRepeat)
        } else {
            append(currentAmount)
            if (unit != GoalUnit.UNKNOWN) append(" ").append(unit.storage)
        }
    }
}

internal fun buildMarkStates(
    agenda: AgendaModel,
    history: List<AgendaModel>,
    humanize: Humanize,
): List<GoalMarkState> {
    val logsByGoal = agenda.logs
        .filter { it.goalId != null }
        .associateBy { it.goalId }

    val historyTotals: Map<Long, List<Int>> = history
        .flatMap { day ->
            day.logs.mapNotNull { log ->
                val goalId = log.goalId ?: return@mapNotNull null
                if (log.actualAmount == null) return@mapNotNull null
                val total = (log.actualAmount ?: 0) * (log.actualRepeat ?: 1)
                goalId to total
            }
        }
        .groupBy({ it.first }, { it.second })

    return agenda.goals.map { goal ->
        val log = logsByGoal[goal.id]
        val targetTotal = (goal.amount ?: 0) * (goal.repeat ?: 1)
        val factTotal = (log?.actualAmount ?: 0) * (log?.actualRepeat ?: 1)
        val unit = GoalUnit.fromStorage(goal.unit)
        val targetText = humanize.goal(
            title = "",
            unit = unit,
            amount = goal.amount,
            repeat = goal.repeat,
            weight = goal.weight,
        ).best

        val totals = historyTotals[goal.id].orEmpty()
        val averageTotal = if (totals.isEmpty()) {
            0
        } else {
            (totals.sum() / totals.size).coerceIn(0, targetTotal)
        }

        GoalMarkState(
            goalId = goal.id,
            title = goal.title,
            targetText = targetText,
            targetTotal = targetTotal,
            currentTotal = factTotal.coerceIn(0, targetTotal),
            averageTotal = averageTotal,
            unit = unit,
            amount = goal.amount,
            repeat = goal.repeat,
            weight = goal.weight,
        )
    }
}