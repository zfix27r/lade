package app.lade.humanize.internal.duration

import android.content.Context
import app.lade.humanize.R
import app.lade.humanize.api.Humanized
import javax.inject.Inject

internal class DurationHumanize @Inject constructor(
    private val context: Context,
) {
    fun format(minutes: Int): Humanized {
        if (minutes <= 0) return Humanized("", "")

        val hours = minutes / 60
        val mins = minutes % 60

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
        context.resources.getQuantityString(R.plurals.humanize_duration_min_short, mins, mins)

    private fun shortHours(hours: Int): String =
        context.resources.getQuantityString(R.plurals.humanize_duration_hour_short, hours, hours)

    private fun longMinutes(mins: Int): String =
        context.resources.getQuantityString(R.plurals.humanize_duration_min_long, mins, mins)

    private fun longHours(hours: Int): String =
        context.resources.getQuantityString(R.plurals.humanize_duration_hour_long, hours, hours)
}