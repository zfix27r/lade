package app.lade.humanize.internal.date

import android.content.Context
import app.lade.humanize.R
import app.lade.humanize.api.Humanized
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit

internal class DateHumanize(
    private val context: Context,
) {
    fun format(date: LocalDate, today: LocalDate = LocalDate.now()): Humanized {
        val days = ChronoUnit.DAYS.between(today, date)

        return when (days) {
            -1L -> Humanized(
                short = context.getString(R.string.humanize_date_yesterday_short),
                long = context.getString(R.string.humanize_date_yesterday_long),
            )
            0L -> Humanized(
                short = context.getString(R.string.humanize_date_today_short),
                long = context.getString(R.string.humanize_date_today_long),
            )
            1L -> Humanized(
                short = context.getString(R.string.humanize_date_tomorrow_short),
                long = context.getString(R.string.humanize_date_tomorrow_long),
            )
            else -> {
                val shortText = date.format(MONTH_DAY_SHORT)
                val longText = date.format(MONTH_DAY_LONG)
                Humanized(short = shortText, long = longText)
            }
        }
    }

    companion object {
        private val MONTH_DAY_SHORT: DateTimeFormatter =
            DateTimeFormatter.ofPattern("d MMM")

        private val MONTH_DAY_LONG: DateTimeFormatter =
            DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
    }
}