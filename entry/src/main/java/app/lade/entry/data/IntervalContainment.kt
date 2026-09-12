package app.lade.entry.data

import java.time.LocalTime

object IntervalContainment {
    fun fullyContains(
        outerStart: LocalTime,
        outerEnd: LocalTime,
        innerStart: LocalTime,
        innerEnd: LocalTime,
    ): Boolean {
        if (outerEnd <= outerStart || innerEnd <= innerStart) return false
        return !outerStart.isAfter(innerStart) && !outerEnd.isBefore(innerEnd)
    }

    fun overlaps(
        aStart: LocalTime,
        aEnd: LocalTime,
        bStart: LocalTime,
        bEnd: LocalTime,
    ): Boolean {
        if (aEnd <= aStart || bEnd <= bStart) return false
        return aStart < bEnd && bStart < aEnd
    }
}