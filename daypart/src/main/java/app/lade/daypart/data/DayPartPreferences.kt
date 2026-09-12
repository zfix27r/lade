package app.lade.daypart.data

import android.content.Context
import app.lade.daypart.domain.DayPart
import app.lade.daypart.domain.DayPartClock
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

@Singleton
class DayPartPreferences @Inject constructor(
	@ApplicationContext context: Context,
) {
	private val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

	fun clock(): DayPartClock = DayPartClock(
		morning = read(KEY_MORNING, DayPart.Morning.defaultTime()),
		midday = read(KEY_MIDDAY, DayPart.Midday.defaultTime()),
		evening = read(KEY_EVENING, DayPart.Evening.defaultTime()),
	)

	fun setTime(part: DayPart, time: LocalTime) {
		val key = when (part) {
			DayPart.Morning -> KEY_MORNING
			DayPart.Midday -> KEY_MIDDAY
			DayPart.Evening -> KEY_EVENING
		}
		prefs.edit { putString(key, time.toString()) }
	}

	private fun read(key: String, fallback: LocalTime): LocalTime {
		val raw = prefs.getString(key, null) ?: return fallback
		return runCatching { LocalTime.parse(raw) }.getOrDefault(fallback)
	}

	companion object {
		private const val PREFS = "lade_daypart"
		private const val KEY_MORNING = "morning"
		private const val KEY_MIDDAY = "midday"
		private const val KEY_EVENING = "evening"
	}
}