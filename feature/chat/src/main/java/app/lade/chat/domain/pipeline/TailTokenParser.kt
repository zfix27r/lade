package app.lade.chat.domain.pipeline

import app.lade.chat.domain.ChatActual
import app.lade.daypart.domain.DayPart
import app.lade.daypart.domain.DayPartClock
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * Parses date/time/daypart/duration and title from slot leftover tokens.
 * Shared by target Tail stage (legacy candidate stages duplicate this until cut).
 */
object TailTokenParser {
	data class Input(
		val tokens: List<String>,
		val today: LocalDate,
		val dayPartClock: DayPartClock,
		val titleHint: String?,
		val userIntent: UserIntent?,
		val slotFacts: List<SlotFact> = emptyList(),
	)

	fun parse(input: Input): TailResult {
		var date = input.today
		var startTime: LocalTime? = null
		var durationMin: Int? = null
		val titleWords = mutableListOf<String>()
		var i = 0
		val tokens = input.tokens
		while (i < tokens.size) {
			val token = tokens[i]
			when {
				token in ParseTokenHelpers.RELATIVE_DATES -> {
					date = ParseTokenHelpers.relativeDate(token, input.today)
					i++
				}
				token in ParseTokenHelpers.DAYPART_WORDS -> {
					startTime = dayPartTime(token, input.dayPartClock)
					i++
				}
				parseTime(token) != null -> {
					startTime = parseTime(token)
					i++
				}
				else -> {
					val number = token.replace(',', '.').toDoubleOrNull()
					if (number != null) {
						val next = tokens.getOrNull(i + 1)
						if (next != null && next in ParseTokenHelpers.BUILTIN_UNITS) {
							if (ParseTokenHelpers.isDurationUnit(next)) {
								durationMin = number.toInt().coerceAtLeast(5)
							}
							i += 2
						} else if (input.userIntent == UserIntent.TIMED) {
							durationMin = number.toInt().coerceAtLeast(5)
							i++
						} else {
							i++
						}
					} else if (token !in TITLE_SKIP) {
						titleWords += token
						i++
					} else {
						i++
					}
				}
			}
		}
		val title = titleWords.joinToString(" ").ifBlank {
			input.titleHint.orEmpty()
		}.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
		val actuals = slotFactsToActuals(input.slotFacts)
		var resolvedStart = startTime
		if (input.userIntent == UserIntent.TIMED && resolvedStart == null) {
			resolvedStart = defaultStartNow()
		}
		val endTime = timedEndTime(input.userIntent, resolvedStart, durationMin)
		return TailResult(
			date = date,
			startTime = resolvedStart,
			endTime = endTime,
			durationMin = durationMin,
			title = title,
			actuals = actuals,
		)
	}

	private fun defaultStartNow(): LocalTime {
		val now = LocalTime.now().withSecond(0).withNano(0)
		val snapped = (now.hour * 60 + now.minute) / 5 * 5
		return LocalTime.ofSecondOfDay(snapped * 60L)
	}

	private fun slotFactsToActuals(facts: List<SlotFact>): List<ChatActual> =
		facts.map { fact ->
			ChatActual(
				key = fact.key,
				value = fact.amount,
				unit = fact.unit,
			)
		}

	private fun timedEndTime(
		userIntent: UserIntent?,
		startTime: LocalTime?,
		durationMin: Int?,
	): LocalTime? {
		if (userIntent != UserIntent.TIMED) return null
		val start = startTime ?: return null
		val minutes = (durationMin ?: DEFAULT_TIMED_MINUTES).coerceIn(5, 24 * 60 - 1)
		val endMin = (start.hour * 60 + start.minute + minutes).coerceAtMost(24 * 60 - 1)
		return LocalTime.ofSecondOfDay(endMin * 60L)
	}

	private fun dayPartTime(token: String, clock: DayPartClock): LocalTime = when (token) {
		"утро", "morning" -> clock.timeOf(DayPart.Morning)
		"обед", "день", "midday", "noon" -> clock.timeOf(DayPart.Midday)
		else -> clock.timeOf(DayPart.Evening)
	}

	private fun parseTime(token: String): LocalTime? {
		for (pattern in TIME_PATTERNS) {
			try {
				return LocalTime.parse(token, pattern)
			} catch (_: DateTimeParseException) {
				// next
			}
		}
		return null
	}

	private val TITLE_SKIP = ParseTokenHelpers.RELATIVE_DATES +
		ParseTokenHelpers.DAYPART_WORDS +
		ParseTokenHelpers.BUILTIN_UNITS +
		setOf("с", "и", "а", "на", "по", "the", "a", "with")

	private const val DEFAULT_TIMED_MINUTES = 60

	private val TIME_PATTERNS = listOf(
		DateTimeFormatter.ofPattern("H:mm"),
		DateTimeFormatter.ofPattern("HH:mm"),
	)
}
