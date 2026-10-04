package app.lade.calendardata.internal.mapper

import app.lade.agendastore.goal.GoalEntity
import app.lade.agendastore.log.LogEntity

internal fun GoalEntity.isDone(log: LogEntity?): Boolean {
    val targetAmount = amount
    val actual = log?.actualAmount ?: 0
    val amountReached = when {
        targetAmount == null -> actual > 0
        else -> actual >= targetAmount
    }
    val repeatTarget = repeat
    val actualRepeat = log?.actualRepeat ?: 0
    val repeatReached = when {
        repeatTarget == null -> true
        else -> actualRepeat >= repeatTarget
    }
    return amountReached && repeatReached
}