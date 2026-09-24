package app.lade.humanize.internal.rrule

import android.content.Context
import app.lade.humanize.R
import app.lade.humanize.api.Humanized
import app.lade.recurrence.api.DaysOfWeekFlags
import app.lade.recurrence.api.RecurrenceDraft
import app.lade.recurrence.api.RecurrencePreset

internal class RruleHumanize(
    private val context: Context,
) {
    fun format(rrule: String): Humanized {
        if (rrule.isBlank()) return Humanized("", "")
        val draft = RecurrenceDraft.fromRrule(rrule)
        return when (draft.preset) {
            RecurrencePreset.None -> Humanized("", "")
            RecurrencePreset.Daily -> daily()
            RecurrencePreset.EveryNDays -> everyNDays(draft.interval)
            RecurrencePreset.Weekdays -> weekdays()
            RecurrencePreset.Weekly -> weekly(draft.interval, draft.daysOfWeek)
            RecurrencePreset.Monthly -> monthly(draft.interval)
            RecurrencePreset.Yearly -> yearly(draft.interval)
        }
    }

    private fun daily(): Humanized = Humanized(
        short = context.getString(R.string.humanize_rrule_daily_short),
        long = context.getString(R.string.humanize_rrule_daily_long),
    )

    private fun everyNDays(interval: Int): Humanized = Humanized(
        short = "",
        long = context.resources.getQuantityString(
            R.plurals.humanize_rrule_every_n_days_long,
            interval,
            interval,
        ),
    )

    private fun weekdays(): Humanized = Humanized(
        short = context.getString(R.string.humanize_rrule_weekdays_short),
        long = context.getString(R.string.humanize_rrule_weekdays_long),
    )

    private fun weekly(interval: Int, daysOfWeek: Int): Humanized {
        val days = selectedDays(daysOfWeek)
        if (days.isEmpty()) return Humanized("", "")

        if (interval == 1 && days.size == 1) {
            return Humanized(
                short = context.getString(R.string.humanize_rrule_weekly_one_short, days.first().short),
                long = context.getString(R.string.humanize_rrule_weekly_one_long, days.first().long),
            )
        }

        if (interval == 1) {
            val shortDays = days.joinToString(", ") { it.short }
            val longDays = days.joinToString(", ") { it.long }
            return Humanized(
                short = context.getString(R.string.humanize_rrule_weekly_many_short, shortDays),
                long = context.getString(R.string.humanize_rrule_weekly_many_long, longDays),
            )
        }

        val shortDays = days.joinToString(", ") { it.short }
        val longDays = days.joinToString(", ") { it.long }
        return Humanized(
            short = "",
            long = context.resources.getQuantityString(
                R.plurals.humanize_rrule_every_n_weeks_long,
                interval,
                interval,
                longDays,
            ),
        )
    }

    private fun monthly(interval: Int): Humanized {
        if (interval == 1) {
            return Humanized(
                short = context.getString(R.string.humanize_rrule_monthly_short),
                long = context.getString(R.string.humanize_rrule_monthly_long),
            )
        }
        return Humanized(
            short = "",
            long = context.resources.getQuantityString(
                R.plurals.humanize_rrule_every_n_months_long,
                interval,
                interval,
            ),
        )
    }

    private fun yearly(interval: Int): Humanized {
        if (interval == 1) {
            return Humanized(
                short = context.getString(R.string.humanize_rrule_yearly_short),
                long = context.getString(R.string.humanize_rrule_yearly_long),
            )
        }
        return Humanized(
            short = "",
            long = context.resources.getQuantityString(
                R.plurals.humanize_rrule_every_n_years_long,
                interval,
                interval,
            ),
        )
    }

    private fun selectedDays(flags: Int): List<DayLabel> =
        DaysOfWeekFlags.ALL_DAY_FLAGS
            .filter { DaysOfWeekFlags.has(flags, it) }
            .map { dayLabel(it) }

    private fun dayLabel(flag: Int): DayLabel = when (flag) {
        DaysOfWeekFlags.MONDAY -> DayLabel(
            context.getString(R.string.humanize_day_mon_short),
            context.getString(R.string.humanize_day_mon_long),
        )
        DaysOfWeekFlags.TUESDAY -> DayLabel(
            context.getString(R.string.humanize_day_tue_short),
            context.getString(R.string.humanize_day_tue_long),
        )
        DaysOfWeekFlags.WEDNESDAY -> DayLabel(
            context.getString(R.string.humanize_day_wed_short),
            context.getString(R.string.humanize_day_wed_long),
        )
        DaysOfWeekFlags.THURSDAY -> DayLabel(
            context.getString(R.string.humanize_day_thu_short),
            context.getString(R.string.humanize_day_thu_long),
        )
        DaysOfWeekFlags.FRIDAY -> DayLabel(
            context.getString(R.string.humanize_day_fri_short),
            context.getString(R.string.humanize_day_fri_long),
        )
        DaysOfWeekFlags.SATURDAY -> DayLabel(
            context.getString(R.string.humanize_day_sat_short),
            context.getString(R.string.humanize_day_sat_long),
        )
        DaysOfWeekFlags.SUNDAY -> DayLabel(
            context.getString(R.string.humanize_day_sun_short),
            context.getString(R.string.humanize_day_sun_long),
        )
        else -> DayLabel("", "")
    }

    private data class DayLabel(val short: String, val long: String)
}