package app.lade.schedule.ui

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import app.lade.resources.R
import app.lade.temporal.api.DaysOfWeekFlags
import app.lade.temporal.api.RecurrenceDraft
import app.lade.temporal.api.RecurrencePreset

@Composable
fun RecurrencePreset.label(): String = stringResource(labelRes())

@StringRes
fun RecurrencePreset.labelRes(): Int = when (this) {
	RecurrencePreset.Daily -> R.string.recurrence_daily
	RecurrencePreset.Weekdays -> R.string.recurrence_weekdays
	RecurrencePreset.Weekly -> R.string.recurrence_weekly
	RecurrencePreset.EveryNDays -> R.string.recurrence_every_n_days
	RecurrencePreset.Monthly -> R.string.recurrence_monthly
	RecurrencePreset.Yearly -> R.string.recurrence_yearly
    RecurrencePreset.None -> R.string.recurrence_none
}

@Composable
fun RecurrenceDraft.label(): String = preset.label()

val weekdayLabelRes: List<Pair<Int, Int>> = listOf(
	DaysOfWeekFlags.MONDAY to R.string.weekday_mon,
	DaysOfWeekFlags.TUESDAY to R.string.weekday_tue,
	DaysOfWeekFlags.WEDNESDAY to R.string.weekday_wed,
	DaysOfWeekFlags.THURSDAY to R.string.weekday_thu,
	DaysOfWeekFlags.FRIDAY to R.string.weekday_fri,
	DaysOfWeekFlags.SATURDAY to R.string.weekday_sat,
	DaysOfWeekFlags.SUNDAY to R.string.weekday_sun,
)
