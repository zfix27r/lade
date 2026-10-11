package app.lade.humanize.internal.time

import app.lade.humanize.api.Humanized
import java.time.LocalTime

internal class TimeRangeHumanize(
    private val timeHumanize: TimeHumanize,
) {
    fun format(from: LocalTime, to: LocalTime): Humanized {
        if (from == to) return timeHumanize.format(from)

        val fromShort = timeHumanize.format(from).short
        val toShort = timeHumanize.format(to).short
        val fromLong = timeHumanize.format(from).long
        val toLong = timeHumanize.format(to).long

        return Humanized(
            short = "$fromShort – $toShort",
            long = "$fromLong – $toLong",
        )
    }
}