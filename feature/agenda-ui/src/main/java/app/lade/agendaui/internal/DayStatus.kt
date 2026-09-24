package app.lade.agendaui.internal

import app.lade.agenda.api.agenda.AgendaModel
import java.time.LocalDate

enum class DayStatus {
    DONE,
    PARTIAL,
    SKIPPED,
    NOT_MARKED,
    PLANNED,
    NO_TARGET,
}

data class DayStats(
    val date: LocalDate,
    val targetTotal: Int,
    val factTotal: Int,
    val status: DayStatus,
    val ratio: Float,
)

data class PeriodStats(
    val days: List<DayStats>,
    val planned: Int,
    val done: Int,
    val partial: Int,
    val skipped: Int,
    val notMarked: Int,
)

internal fun buildPeriodStats(
    entries: List<AgendaModel>,
    today: LocalDate,
): PeriodStats {
    val days = entries
        .sortedBy { it.date }
        .map { it.toDayStats(today) }

    return PeriodStats(
        days = days,
        planned = days.count { it.targetTotal > 0 },
        done = days.count { it.status == DayStatus.DONE },
        partial = days.count { it.status == DayStatus.PARTIAL },
        skipped = days.count { it.status == DayStatus.SKIPPED },
        notMarked = days.count { it.status == DayStatus.NOT_MARKED },
    )
}

private fun AgendaModel.toDayStats(today: LocalDate): DayStats {
    val targetTotal = goals.sumOf { (it.amount ?: 0) * (it.repeat ?: 1) }
    val factTotal = logs.sumOf { (it.actualAmount ?: 0) * (it.actualRepeat ?: 1) }
    val ratio = if (targetTotal > 0) {
        (factTotal.toFloat() / targetTotal).coerceIn(0f, 1f)
    } else 0f

    val status = when {
        date > today -> DayStatus.PLANNED
        targetTotal == 0 -> DayStatus.NO_TARGET
        factTotal >= targetTotal -> DayStatus.DONE
        factTotal > 0 -> DayStatus.PARTIAL
        logs.isNotEmpty() -> DayStatus.SKIPPED
        else -> DayStatus.NOT_MARKED
    }

    return DayStats(
        date = date,
        targetTotal = targetTotal,
        factTotal = factTotal,
        status = status,
        ratio = ratio,
    )
}