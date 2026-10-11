package app.lade.parser.internal.goal

import app.lade.parser.api.ParserContract
import app.lade.parser.api.ParserGoalModel
import kotlin.text.get

internal class ParserGoalsExtractor {

    data class Result(
        val goals: List<ParserGoalModel>,
        val remaining: String,
    )

    fun extract(raw: String): Result {
        val goals = mutableListOf<ParserGoalModel>()
        val leftovers = mutableListOf<String>()

        raw.split(",").forEach { segment ->
            val trimmed = segment.trim()
            if (trimmed.isEmpty()) return@forEach
            val goal = parseSegment(trimmed)
            if (goal != null) goals += goal else leftovers += trimmed
        }

        return Result(goals, leftovers.joinToString(", "))
    }

    private fun parseSegment(segment: String): ParserGoalModel? {
        val match = SEGMENT_REGEX.find(segment) ?: return null
        val title = match.groups["label"]?.value?.trim().orEmpty()
        val amount = match.groups["amount"]?.value?.toIntOrNull() ?: return null
        val unitText = match.groups["unit"]?.value.orEmpty()
        val per = match.groups["per"]?.value?.toIntOrNull()
        val weightSign = match.groups["wsign"]?.value
        val weightValue = match.groups["wvalue"]?.value?.replace(',', '.')?.toDoubleOrNull()

        val weight = if (weightSign != null && weightValue != null) {
            if (weightSign == "-") -weightValue else weightValue
        } else null

        val unit = when {
            unitText.isNotBlank() -> resolveUnit(unitText) ?: return null
            per != null -> "rep"
            else -> return null
        }

        return ParserGoalModel(
            amount = ParserContract.found(amount.toString()),
            unit = ParserContract.found(unit),
            title = if (title.isBlank()) ParserContract.skip() else ParserContract.found(title),
            repeats = per?.let { ParserContract.found(it.toString()) } ?: ParserContract.skip(),
            weight = weight?.let { ParserContract.found(it.toString()) } ?: ParserContract.skip(),
        )
    }

    private fun resolveUnit(text: String): String? {
        if (text.isBlank()) return null
        return UNITS.firstOrNull { it.regex.matches(text) }?.unit
    }

    private data class UnitEntry(val unit: String, val regex: Regex)

    companion object {
        private val UNITS = listOf(
            UnitEntry("km", Regex("""километр\p{L}*|км""", RegexOption.IGNORE_CASE)),
            UnitEntry("min", Regex("""минут\p{L}*|мин""", RegexOption.IGNORE_CASE)),
            UnitEntry("ml", Regex("""мл""", RegexOption.IGNORE_CASE)),
            UnitEntry("m", Regex("""метр\p{L}*|м""", RegexOption.IGNORE_CASE)),
            UnitEntry("kg", Regex("""килограмм\p{L}*|кг""", RegexOption.IGNORE_CASE)),
            UnitEntry("hour", Regex("""час\p{L}*""", RegexOption.IGNORE_CASE)),
            UnitEntry("liter", Regex("""литр\p{L}*|л""", RegexOption.IGNORE_CASE)),
            UnitEntry("step", Regex("""шаг\p{L}*""", RegexOption.IGNORE_CASE)),
            UnitEntry("glass", Regex("""стакан\p{L}*""", RegexOption.IGNORE_CASE)),
            UnitEntry("rep", Regex("""раз|повтор\p{L}*""", RegexOption.IGNORE_CASE)),
            UnitEntry("approach", Regex("""подход\p{L}*|сет\p{L}*""", RegexOption.IGNORE_CASE)),
            UnitEntry("cal", Regex("""ккал|кал\p{L}*""", RegexOption.IGNORE_CASE)),
            UnitEntry("lap", Regex("""кругов?\b""", RegexOption.IGNORE_CASE)),
        )

        private const val UNIT_PATTERN =
            """километр\p{L}*|км|минут\p{L}*|мин|мл|метр\p{L}*|м|килограмм\p{L}*|кг|час\p{L}*|литр\p{L}*|л|шаг\p{L}*|стакан\p{L}*|раз|повтор\p{L}*|подход\p{L}*|сет\p{L}*|ккал|кал\p{L}*|кругов?\b"""

        private val SEGMENT_REGEX = Regex(
            """^\s*(?<label>[а-яёa-z][а-яёa-z\s]*)?\s*(?<amount>\d{1,6})\s*(?<unit>$UNIT_PATTERN)?\s*(?:(?:по|[хx×])\s*(?<per>\d{1,3}))?\s*(?:(?<wsign>[+-])\s*(?<wvalue>\d+(?:[.,]\d+)?)\s*(?:кг|kg))?\s*$""",
            RegexOption.IGNORE_CASE,
        )
    }
}