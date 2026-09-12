package app.lade.daypart.domain

import java.time.LocalTime

enum class DayPart {
	Morning,
	Midday,
	Evening,
	;

	fun defaultTime(): LocalTime = when (this) {
		Morning -> LocalTime.of(9, 0)
		Midday -> LocalTime.of(12, 0)
		Evening -> LocalTime.of(18, 0)
	}
}
