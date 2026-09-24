package app.lade.humanize.internal.time

import app.lade.humanize.api.Humanized
import java.time.LocalTime
import java.time.format.DateTimeFormatter

internal class TimeHumanize {
    fun format(time: LocalTime): Humanized {
        val text = time.format(FORMATTER)
        return Humanized(short = text, long = text)
    }

    companion object {
        private val FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    }
}