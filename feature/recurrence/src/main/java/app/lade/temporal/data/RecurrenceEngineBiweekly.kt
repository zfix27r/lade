package app.lade.temporal.data

import app.lade.temporal.api.RecurrenceEngine
import biweekly.ICalVersion
import biweekly.io.ParseContext
import biweekly.io.scribe.property.RecurrenceRuleScribe
import biweekly.parameter.ICalParameters
import biweekly.util.com.google.ical.compat.javautil.DateIterator
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecurrenceEngineBiweekly @Inject constructor() : RecurrenceEngine {
	private val scribe = RecurrenceRuleScribe()

	override fun isDue(rrule: String, date: LocalDate, dtStart: LocalDate): Boolean {
		val normalized = normalize(rrule)
		if (normalized.isBlank()) return false
		return occurrencesBetween(normalized, date, date, dtStart).isNotEmpty()
	}

	override fun occurrencesBetween(
		rrule: String,
		fromInclusive: LocalDate,
		toInclusive: LocalDate,
		dtStart: LocalDate,
	): List<LocalDate> {
		val normalized = normalize(rrule)
		if (normalized.isBlank()) return emptyList()
		val property = try {
			val context = ParseContext().apply { version = ICalVersion.V2_0 }
			scribe.parseText(normalized, null, ICalParameters(), context)
		} catch (_: Exception) {
			return emptyList()
		}
		val zone = ZoneId.systemDefault()
		val tz = TimeZone.getTimeZone(zone)
		val startDate = Date.from(dtStart.atStartOfDay(zone).toInstant())
		val iterator: DateIterator = property.getDateIterator(startDate, tz)
		val fromInstant = fromInclusive.atStartOfDay(zone).toInstant()
		iterator.advanceTo(Date.from(fromInstant))
		val toExclusive = toInclusive.plusDays(1).atStartOfDay(zone).toInstant()
		val result = mutableListOf<LocalDate>()
		while (iterator.hasNext()) {
			val next = iterator.next()
			val instant = next.toInstant()
			if (!instant.isBefore(toExclusive)) break
			result += instant.atZone(zone).toLocalDate()
			if (result.size > 370) break
		}
		return result
	}

	private fun normalize(rrule: String): String =
		rrule.trim()
			.removePrefix("RRULE:")
			.removePrefix("rrule:")
			.trim()
}