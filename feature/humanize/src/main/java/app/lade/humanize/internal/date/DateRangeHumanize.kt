package app.lade.humanize.internal.date

import android.content.Context
import app.lade.humanize.R
import app.lade.humanize.api.Humanized
import java.time.LocalDate
import java.time.temporal.ChronoUnit

internal class DateRangeHumanize(
    private val context: Context,
    private val dateHumanize: DateHumanize,
) {
    fun format(from: LocalDate, to: LocalDate, today: LocalDate = LocalDate.now()): Humanized {
        if (from == to) return dateHumanize.format(from, today)

        val fromShort = dateHumanize.format(from, today).short
        val toShort = dateHumanize.format(to, today).short
        val fromLong = dateHumanize.format(from, today).long
        val toLong = dateHumanize.format(to, today).long

        val short = if (sameMonthAndYear(from, to)) {
            context.getString(
                R.string.humanize_date_range_same_month_short,
                from.dayOfMonth.toString(),
                toShort,
            )
        } else {
            context.getString(
                R.string.humanize_date_range_short,
                fromShort,
                toShort,
            )
        }

        val long = context.getString(
            R.string.humanize_date_range_long,
            fromLong,
            toLong,
        )

        return Humanized(short = short, long = long)
    }

    private fun sameMonthAndYear(a: LocalDate, b: LocalDate): Boolean =
        a.year == b.year && a.month == b.month &&
                ChronoUnit.DAYS.between(a, b) in 1..30
}