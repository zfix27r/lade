package app.lade.daypart.domain

import java.time.LocalTime

data class DayPartClock(
    val morning: LocalTime = DayPart.Morning.defaultTime(),
    val midday: LocalTime = DayPart.Midday.defaultTime(),
    val evening: LocalTime = DayPart.Evening.defaultTime(),
) {
    fun timeOf(part: DayPart): LocalTime = when (part) {
        DayPart.Morning -> morning
        DayPart.Midday -> midday
        DayPart.Evening -> evening
    }

    companion object {
        val DEFAULT = DayPartClock()
    }
}