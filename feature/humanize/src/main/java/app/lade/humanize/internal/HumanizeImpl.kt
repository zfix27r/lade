package app.lade.humanize.internal

import android.content.Context
import app.lade.agenda.api.goal.GoalUnit
import app.lade.entrykind.EntryKind
import app.lade.humanize.api.Humanize
import app.lade.humanize.api.Humanized
import app.lade.humanize.internal.alarm.AlarmHumanize
import app.lade.humanize.internal.date.DateHumanize
import app.lade.humanize.internal.duration.DurationHumanize
import app.lade.humanize.internal.entrykind.EntryKindHumanize
import app.lade.humanize.internal.goal.GoalHumanize
import app.lade.humanize.internal.reminder.ReminderHumanize
import app.lade.humanize.internal.rrule.RruleHumanize
import app.lade.humanize.internal.time.TimeHumanize
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class HumanizeImpl @Inject constructor(
    @ApplicationContext context: Context,
) : Humanize {

    private val rruleHumanize = RruleHumanize(context)
    private val dateHumanize = DateHumanize(context)
    private val timeHumanize = TimeHumanize()
    private val durationHumanize = DurationHumanize(context)
    private val goalHumanize = GoalHumanize(context)
    private val alarmHumanize = AlarmHumanize(context)
    private val reminderHumanize = ReminderHumanize(context)
    private val entryKindHumanize = EntryKindHumanize(context)

    override fun rrule(rrule: String): Humanized = rruleHumanize.format(rrule)
    override fun date(date: LocalDate): Humanized = dateHumanize.format(date)
    override fun time(time: LocalTime): Humanized = timeHumanize.format(time)
    override fun duration(minutes: Int): Humanized = durationHumanize.format(minutes)
    override fun goal(title: String, unit: GoalUnit, amount: Int?, repeat: Int?, weight: Double?): Humanized =
        goalHumanize.format(title, unit, amount, repeat, weight)
    override fun alarm(time: LocalTime, mode: String): Humanized =
        alarmHumanize.format(time, mode)
    override fun reminder(minutesBefore: Int): Humanized =
        reminderHumanize.format(minutesBefore)
    override fun entryKind(kind: EntryKind): Humanized =
        entryKindHumanize.format(kind)
}