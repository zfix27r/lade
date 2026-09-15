package app.lade.chat.domain.pipeline

/**
 * Shared token helpers for candidate stages (dates, daypart, numbers, units).
 */
internal object ParseTokenHelpers {
	val WHITESPACE = Regex("\\s+")
	val RELATIVE_DATES = setOf("сегодня", "завтра", "вчера", "today", "tomorrow", "yesterday")
	val DAYPART_WORDS = setOf("утро", "обед", "день", "вечер", "morning", "midday", "noon", "evening")
	val DURATION_UNITS = setOf("мин", "минут", "минуты", "min", "m", "час", "часа", "ч", "h")
	val BUILTIN_UNITS = DURATION_UNITS + setOf("км", "km", "раз", "reps", "x")

	fun relativeDate(token: String, today: java.time.LocalDate) = when (token) {
		"сегодня", "today" -> today
		"завтра", "tomorrow" -> today.plusDays(1)
		"вчера", "yesterday" -> today.minusDays(1)
		else -> today
	}

	fun normalizeUnit(alias: String): String = when (alias.lowercase()) {
		"мин", "минут", "минуты", "m", "min" -> "min"
		"км", "km" -> "km"
		"раз", "reps", "x" -> "reps"
		else -> alias.lowercase()
	}

	fun isDurationUnit(alias: String): Boolean = alias.lowercase() in DURATION_UNITS
}
