package app.lade.humanize.api

import app.lade.agenda.api.goal.GoalUnit
import app.lade.entrykind.EntryKind
import java.time.LocalDate
import java.time.LocalTime

interface Humanize {
    fun rrule(rrule: String): Humanized
    fun date(date: LocalDate): Humanized
    fun time(time: LocalTime): Humanized
    fun duration(minutes: Int): Humanized
    fun goal(title: String, unit: GoalUnit, amount: Int?, repeat: Int?, weight: Double?): Humanized
    fun alarm(time: LocalTime, mode: String): Humanized
    fun reminder(minutesBefore: Int): Humanized
    fun entryKind(kind: EntryKind): Humanized
}