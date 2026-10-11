package app.lade.calendar.internal.list.card.expandable

internal fun adaptiveGoalStep(plannedAmount: Int): Int = when {
    plannedAmount <= 0 -> 1
    plannedAmount <= 30 -> 1
    plannedAmount <= 100 -> 5
    plannedAmount <= 1000 -> 10
    plannedAmount <= 10000 -> 100
    plannedAmount <= 100000 -> 500
    else -> 1000
}

internal fun snapToStep(value: Int, step: Int): Int {
    if (step <= 0) return value
    return ((value + step / 2) / step) * step
}