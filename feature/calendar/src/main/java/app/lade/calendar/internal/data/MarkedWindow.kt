package app.lade.calendar.internal.data

import java.time.LocalDate

enum class MarkedStep { WEEK, MONTH }

data class MarkedWindow(
    val anchor: LocalDate,
    val step: MarkedStep,
    val radius: Int = 3,
) {
    val from: LocalDate = shift(anchor, -radius)
    val to: LocalDate = shift(anchor, radius)

    fun covers(date: LocalDate): Boolean {
        val margin = radius - 1
        return !date.isBefore(shift(anchor, -margin)) &&
                !date.isAfter(shift(anchor, margin))
    }

    private fun shift(date: LocalDate, steps: Int): LocalDate = when (step) {
        MarkedStep.WEEK -> date.plusWeeks(steps.toLong())
        MarkedStep.MONTH -> date.plusMonths(steps.toLong())
    }
}