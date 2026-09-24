package app.lade.recurrence.api

data class RecurrenceDraft(
	val preset: RecurrencePreset = RecurrencePreset.None,
	val interval: Int = 1,
	val daysOfWeek: Int = DaysOfWeekFlags.WEEKDAYS,
) {
	fun toRrule(): String {
		if (preset == RecurrencePreset.None) return ""
		val n = interval.coerceAtLeast(1)
		val intervalPart = if (n == 1) "" else ";INTERVAL=$n"
		return when (preset) {
			RecurrencePreset.None -> ""
			RecurrencePreset.Daily -> "FREQ=DAILY"
			RecurrencePreset.EveryNDays -> "FREQ=DAILY;INTERVAL=$n"
			RecurrencePreset.Weekdays -> "FREQ=WEEKLY;BYDAY=MO,TU,WE,TH,FR"
			RecurrencePreset.Weekly -> {
				val byDay = DaysOfWeekFlags.toByDay(daysOfWeek).ifBlank { "MO" }
				"FREQ=WEEKLY$intervalPart;BYDAY=$byDay"
			}
			RecurrencePreset.Monthly -> "FREQ=MONTHLY$intervalPart"
			RecurrencePreset.Yearly -> "FREQ=YEARLY$intervalPart"
		}
	}

	fun hasValidDays(): Boolean = when (preset) {
		RecurrencePreset.None -> false
		RecurrencePreset.Weekly -> daysOfWeek != 0
		else -> true
	}

	fun coercePreset(allowed: List<RecurrencePreset>): RecurrenceDraft {
		if (preset in allowed) return this
		return when {
			RecurrencePreset.None in allowed -> RecurrenceDraft(RecurrencePreset.None)
			RecurrencePreset.Weekdays in allowed && daysOfWeek == DaysOfWeekFlags.WEEKDAYS ->
				copy(preset = RecurrencePreset.Weekdays, interval = 1)
			RecurrencePreset.Daily in allowed &&
					(daysOfWeek == DaysOfWeekFlags.ALL || daysOfWeek == 0) ->
				copy(preset = RecurrencePreset.Daily, interval = 1)
			RecurrencePreset.Weekly in allowed ->
				copy(
					preset = RecurrencePreset.Weekly,
					daysOfWeek = daysOfWeek.ifZero(DaysOfWeekFlags.WEEKDAYS),
				)
			else -> RecurrenceDraft(allowed.first())
		}
	}

	companion object {
		fun fromRrule(rrule: String?): RecurrenceDraft {
			if (rrule.isNullOrBlank()) return RecurrenceDraft()
			if (rrule.contains("biweekly.util.Recurrence@")) return RecurrenceDraft()
			val body = rrule.trim().removePrefix("RRULE:").removePrefix("rrule:")
			val parts = body.split(';').associate { part ->
				val idx = part.indexOf('=')
				if (idx < 0) "" to ""
				else part.substring(0, idx).uppercase() to part.substring(idx + 1)
			}
			val freq = parts["FREQ"]?.uppercase().orEmpty()
			val interval = parts["INTERVAL"]?.toIntOrNull()?.coerceAtLeast(1) ?: 1
			val days = DaysOfWeekFlags.fromByDay(parts["BYDAY"].orEmpty())
			return when (freq) {
				"DAILY" -> if (interval == 1) {
					RecurrenceDraft(RecurrencePreset.Daily, 1, DaysOfWeekFlags.ALL)
				} else {
					RecurrenceDraft(RecurrencePreset.EveryNDays, interval, DaysOfWeekFlags.ALL)
				}
				"WEEKLY" -> {
					if (days == DaysOfWeekFlags.WEEKDAYS && interval == 1) {
						RecurrenceDraft(RecurrencePreset.Weekdays, 1, days)
					} else {
						RecurrenceDraft(
							RecurrencePreset.Weekly,
							interval,
							days.ifZero(DaysOfWeekFlags.WEEKDAYS),
						)
					}
				}
				"MONTHLY" -> RecurrenceDraft(RecurrencePreset.Monthly, interval)
				"YEARLY" -> RecurrenceDraft(RecurrencePreset.Yearly, interval)
				else -> RecurrenceDraft()
			}
		}
	}
}

private fun Int.ifZero(fallback: Int) = if (this == 0) fallback else this