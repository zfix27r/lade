package app.lade.calendar.internal.week.data

import java.time.temporal.WeekFields
import java.util.Locale

internal fun weekFieldsFor(locale: Locale): WeekFields = WeekFields.of(locale)