package app.lade.calendar.internal.data

import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit

internal data class MarkedRange(
    val from: LocalDate,
    val to: LocalDate,
) {
    val widthInMonths: Long = ChronoUnit.MONTHS.between(from, to)

    fun contains(date: LocalDate, marginMonths: Long): Boolean =
        !date.isBefore(from.plusMonths(marginMonths)) &&
        !date.isAfter(to.minusMonths(marginMonths))

    fun extendedToward(anchor: LocalDate, extraMonths: Long): MarkedRange {
        val newFrom =
            if (anchor.isBefore(from.plusMonths(extraMonths))) anchor.minusMonths(extraMonths)
            else from
        val newTo =
            if (anchor.isAfter(to.minusMonths(extraMonths))) anchor.plusMonths(extraMonths)
            else to
        return MarkedRange(newFrom, newTo)
    }

    fun trimAround(anchor: LocalDate, maxMonths: Long): MarkedRange {
        if (widthInMonths <= maxMonths) return this
        val half = maxMonths / 2
        return MarkedRange(
            from = maxOf(from, anchor.minusMonths(half)),
            to = minOf(to, anchor.plusMonths(half)),
        )
    }

    companion object {
        fun month(anchor: LocalDate): MarkedRange {
            val ym = YearMonth.from(anchor)
            return MarkedRange(from = ym.atDay(1), to = ym.atEndOfMonth())
        }

        fun aroundMonth(anchor: LocalDate, radius: Long): MarkedRange {
            val ym = YearMonth.from(anchor)
            return MarkedRange(
                from = ym.atDay(1).minusMonths(radius),
                to = ym.atEndOfMonth().plusMonths(radius),
            )
        }
    }
}

internal object MarkedRangeDefaults {
    const val INITIAL_RADIUS_MONTHS: Long = 3L
    const val MARGIN_MONTHS: Long = 2L
    const val EXTRA_MONTHS: Long = 2L
    const val MAX_MONTHS: Long = 24L
}