package app.lade.daypart.domain

import java.time.LocalTime

fun DayPartClock.matchingPart(time: LocalTime): DayPart? =
    DayPart.entries.find { timeOf(it) == time }
