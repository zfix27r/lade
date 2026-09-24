package app.lade.recurrence.api

/** Monday = bit 0 … Sunday = bit 6. */
object DaysOfWeekFlags {
	const val MONDAY = 1 shl 0
	const val TUESDAY = 1 shl 1
	const val WEDNESDAY = 1 shl 2
	const val THURSDAY = 1 shl 3
	const val FRIDAY = 1 shl 4
	const val SATURDAY = 1 shl 5
	const val SUNDAY = 1 shl 6
	const val ALL = MONDAY or TUESDAY or WEDNESDAY or THURSDAY or FRIDAY or SATURDAY or SUNDAY
	const val WEEKDAYS = MONDAY or TUESDAY or WEDNESDAY or THURSDAY or FRIDAY

	val ALL_DAY_FLAGS = listOf(
		MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY,
	)

	private val ICAL_DAYS = listOf(
		MONDAY to "MO",
		TUESDAY to "TU",
		WEDNESDAY to "WE",
		THURSDAY to "TH",
		FRIDAY to "FR",
		SATURDAY to "SA",
		SUNDAY to "SU",
	)

	fun has(flags: Int, day: Int): Boolean = flags and day != 0

	fun toggle(flags: Int, day: Int): Int =
		if (has(flags, day)) flags and day.inv() else flags or day

	fun toByDay(flags: Int): String =
		ICAL_DAYS.filter { has(flags, it.first) }.joinToString(",") { it.second }

	fun fromByDay(byDay: String): Int {
		if (byDay.isBlank()) return 0
		var flags = 0
		byDay.split(',').forEach { token ->
			flags = flags or when (token.trim().takeLast(2).uppercase()) {
				"MO" -> MONDAY
				"TU" -> TUESDAY
				"WE" -> WEDNESDAY
				"TH" -> THURSDAY
				"FR" -> FRIDAY
				"SA" -> SATURDAY
				"SU" -> SUNDAY
				else -> 0
			}
		}
		return flags
	}
}