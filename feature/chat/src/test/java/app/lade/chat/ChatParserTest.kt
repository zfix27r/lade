package app.lade.chat

import app.lade.chat.api.FieldKey
import app.lade.chat.api.FieldValue
import app.lade.chat.api.ParseResult
import app.lade.chat.internal.pipeline.ChatParseOrchestrator
import app.lade.draftdata.DraftModel
import app.lade.entrykind.EntryKind
import app.lade.entrykind.EntryKindInput
import app.lade.entrykind.EntryKindResolver
import kotlinx.coroutines.runBlocking
import org.junit.Assert.fail
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

class ChatParserTest {

    private val today: LocalDate = LocalDate.of(2026, 9, 23)
    private val orchestrator = ChatParseOrchestrator(FakeEntryKindResolver)

    @Test
    fun parsePhrases() = runBlocking {
        val json = javaClass.classLoader!!
            .getResourceAsStream("phrases.json")
            .bufferedReader(Charsets.UTF_8)
            .readText()
        val cases = PhraseLoader.load(json)
        val failures = mutableListOf<String>()

        cases.forEach { case ->
            try {
                val actual = orchestrator.run(case.input, emptyList(), today)
                assertCase(case, actual, today)
            } catch (e: AssertionError) {
                failures += "${case.input} → ${e.message}"
            }
        }

        if (failures.isNotEmpty()) {
            fail("${failures.size} из ${cases.size} фраз упали:\n\n${failures.joinToString("\n")}")
        }
    }

    private fun assertCase(case: PhraseCase, result: ParseResult, today: LocalDate) {
        val map = result.fields.associate { it.key to it.value }

        case.dateFrom?.let {
            assertEquals("dateFrom", resolveDate(it, today), (map[FieldKey.DATE_FROM] as? FieldValue.Date)?.value)
        }
        case.dateTo?.let {
            assertEquals("dateTo", resolveDate(it, today), (map[FieldKey.DATE_TO] as? FieldValue.Date)?.value)
        }
        case.timeFrom?.let {
            assertEquals("timeFrom", it, (map[FieldKey.TIME_FROM] as? FieldValue.Time)?.value?.toString())
        }
        case.timeEnd?.let {
            assertEquals("timeEnd", it, (map[FieldKey.TIME_END] as? FieldValue.Time)?.value?.toString())
        }
        case.rrule?.let {
            assertEquals("rrule", it, (map[FieldKey.RRULE] as? FieldValue.Text)?.value)
        }
        case.remaining?.let {
            assertEquals("remaining", it, result.remaining)
        }
    }

    private fun assertEquals(field: String, expected: Any?, actual: Any?) {
        if (expected != actual) {
            throw AssertionError("$field: expected=$expected actual=$actual")
        }
    }

    private fun resolveDate(value: String, today: LocalDate): LocalDate? =
        when (value) {
            "today" -> today
            "tomorrow" -> today.plusDays(1)
            "afterTomorrow" -> today.plusDays(2)
            "yesterday" -> today.minusDays(1)
            "dayAfterYesterday" -> today.minusDays(2)
            "monday" -> today.with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY))
            "tuesday" -> today.with(TemporalAdjusters.nextOrSame(DayOfWeek.TUESDAY))
            "wednesday" -> today.with(TemporalAdjusters.nextOrSame(DayOfWeek.WEDNESDAY))
            "thursday" -> today.with(TemporalAdjusters.nextOrSame(DayOfWeek.THURSDAY))
            "friday" -> today.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY))
            "saturday" -> today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY))
            "sunday" -> today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
            else -> LocalDate.parse(value)
        }

    private object FakeEntryKindResolver : EntryKindResolver {
        override fun resolve(input: EntryKindInput): EntryKind {
            val dateFrom = input.dateFrom
            val dateTo = input.dateTo
            if (dateFrom != null && dateTo != null && dateTo.isAfter(dateFrom))
                return EntryKind.SCHEDULE
            if (!input.rrule.isNullOrBlank() && input.hasGoals) return EntryKind.HABIT
            if (!input.rrule.isNullOrBlank()) return EntryKind.TASK
            if (input.timeFrom != null && input.timeEnd != null) return EntryKind.EVENT
            return EntryKind.TASK
        }
    }
}