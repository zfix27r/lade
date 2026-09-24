package app.lade.humanize.internal.reminder

import android.content.Context
import app.lade.humanize.R
import app.lade.humanize.api.Humanized
import javax.inject.Inject

internal class ReminderHumanize(
    private val context: Context,
) {
    fun format(minutesBefore: Int): Humanized {
        if (minutesBefore <= 0) {
            return Humanized(
                short = context.getString(R.string.humanize_reminder_moment_short),
                long = context.getString(R.string.humanize_reminder_moment_long),
            )
        }

        val hours = minutesBefore / 60
        val mins = minutesBefore % 60

        val short = when {
            hours == 0 -> shortMinutes(mins)
            mins == 0 -> shortHours(hours)
            else -> "${shortHours(hours)} ${shortMinutes(mins)}"
        }

        val long = when {
            hours == 0 -> longMinutes(mins)
            mins == 0 -> longHours(hours)
            else -> "${longHours(hours)} ${longMinutes(mins)}"
        }

        return Humanized(short = short, long = long)
    }

    private fun shortMinutes(mins: Int): String =
        context.resources.getQuantityString(R.plurals.humanize_reminder_min_short, mins, mins)

    private fun shortHours(hours: Int): String =
        context.resources.getQuantityString(R.plurals.humanize_reminder_hour_short, hours, hours)

    private fun longMinutes(mins: Int): String =
        context.resources.getQuantityString(R.plurals.humanize_reminder_min_long, mins, mins)

    private fun longHours(hours: Int): String =
        context.resources.getQuantityString(R.plurals.humanize_reminder_hour_long, hours, hours)
}