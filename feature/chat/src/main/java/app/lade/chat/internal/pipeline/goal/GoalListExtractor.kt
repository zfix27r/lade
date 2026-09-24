package app.lade.chat.internal.pipeline.goal

import app.lade.agenda.api.goal.GoalUnit
import app.lade.draftdata.DraftGoal

internal class GoalListExtractor {

    data class Result(
        val goals: List<DraftGoal>,
        val remaining: String,
    )

    fun extract(raw: String): Result {
        val goals = mutableListOf<DraftGoal>()
        val leftovers = mutableListOf<String>()

        raw.split(",").forEach { segment ->
            val trimmed = segment.trim()
            if (trimmed.isEmpty()) return@forEach
            val goal = parseSegment(trimmed)
            if (goal != null) goals += goal else leftovers += trimmed
        }

        return Result(goals, leftovers.joinToString(", "))
    }

    private fun parseSegment(segment: String): DraftGoal? {
        val match = SEGMENT_REGEX.find(segment) ?: return null
        val title = match.groups["title"]?.value?.trim().orEmpty()
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
            per != null -> GoalUnit.REP
            else -> return null
        }

        val (finalUnit, finalAmount, finalRepeat) = normalize(unit, amount, per)

        return DraftGoal(
            title = title,
            unit = finalUnit,
            amount = finalAmount,
            repeat = finalRepeat,
            weight = weight,
        )
    }

    private fun normalize(unit: GoalUnit, amount: Int, per: Int?): Triple<GoalUnit, Int, Int?> {
        return when {
            unit == GoalUnit.APPROACH && per != null -> Triple(GoalUnit.REP, per, amount)
            per != null -> Triple(GoalUnit.REP, amount, per)
            else -> Triple(unit, amount, null)
        }
    }

    private fun resolveUnit(text: String): GoalUnit? {
        if (text.isBlank()) return null
        return UNITS.firstOrNull { it.regex.matches(text) }?.unit
    }

    private data class UnitEntry(val unit: GoalUnit, val regex: Regex)

    companion object {
        private val UNITS = listOf(
            UnitEntry(GoalUnit.KM, Regex("""километр\p{L}*|км""", RegexOption.IGNORE_CASE)),
            UnitEntry(GoalUnit.MIN, Regex("""минут\p{L}*|мин""", RegexOption.IGNORE_CASE)),
            UnitEntry(GoalUnit.ML, Regex("""мл""", RegexOption.IGNORE_CASE)),
            UnitEntry(GoalUnit.M, Regex("""метр\p{L}*|м""", RegexOption.IGNORE_CASE)),
            UnitEntry(GoalUnit.KG, Regex("""килограмм\p{L}*|кг""", RegexOption.IGNORE_CASE)),
            UnitEntry(GoalUnit.HOUR, Regex("""час\p{L}*""", RegexOption.IGNORE_CASE)),
            UnitEntry(GoalUnit.LITER, Regex("""литр\p{L}*|л""", RegexOption.IGNORE_CASE)),
            UnitEntry(GoalUnit.STEP, Regex("""шаг\p{L}*""", RegexOption.IGNORE_CASE)),
            UnitEntry(GoalUnit.GLASS, Regex("""стакан\p{L}*""", RegexOption.IGNORE_CASE)),
            UnitEntry(GoalUnit.REP, Regex("""раз|повтор\p{L}*""", RegexOption.IGNORE_CASE)),
            UnitEntry(GoalUnit.APPROACH, Regex("""подход\p{L}*|сет\p{L}*""", RegexOption.IGNORE_CASE)),
            UnitEntry(GoalUnit.CAL, Regex("""ккал|кал\p{L}*""", RegexOption.IGNORE_CASE)),
            UnitEntry(GoalUnit.LAP, Regex("""кругов?\b""", RegexOption.IGNORE_CASE)),
        )

        private const val UNIT_PATTERN =
            """километр\p{L}*|км|минут\p{L}*|мин|мл|метр\p{L}*|м|килограмм\p{L}*|кг|час\p{L}*|литр\p{L}*|л|шаг\p{L}*|стакан\p{L}*|раз|повтор\p{L}*|подход\p{L}*|сет\p{L}*|ккал|кал\p{L}*|кругов?\b"""

        private val SEGMENT_REGEX = Regex(
            """^\s*(?<title>[а-яёa-z][а-яёa-z\s]*?)?\s*(?<amount>\d{1,6})\s*(?<unit>$UNIT_PATTERN)?\s*(?:(?:по|[хx×])\s*(?<per>\d{1,3}))?\s*(?:(?<wsign>[+-])\s*(?<wvalue>\d+(?:[.,]\d+)?)\s*(?:кг|kg))?\s*$""",
            RegexOption.IGNORE_CASE,
        )
    }
}