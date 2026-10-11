package app.lade.parser.internal.duration

import app.lade.parser.api.ParserContract
import app.lade.parser.api.ParserModel
import app.lade.parser.internal.rule.ParserPriority
import app.lade.parser.internal.rule.ParserRule
import app.lade.parser.internal.rule.ParserRuleResult

internal class ParserDurationL1Rules : ParserRule {

    override val priority: Int = ParserPriority.L1

    override fun apply(model: ParserModel, remaining: String): ParserRuleResult {
        val entry = model.entry ?: return ParserRuleResult(model, remaining)
        if (!ParserContract.isFind(entry.durationMinutes)) {
            return ParserRuleResult(model, remaining)
        }

        for (rule in RULES) {
            val match = rule.regex.find(remaining) ?: continue
            val minutes = rule.resolve(match) ?: continue
            return ParserRuleResult(
                model.copy(
                    entry = entry.copy(
                        durationMinutes = ParserContract.found(minutes.toString()),
                    ),
                ),
                remaining.removeRange(match.range),
            )
        }
        return ParserRuleResult(model, remaining)
    }

    private data class Rule(
        val regex: Regex,
        val resolve: (MatchResult) -> Int?,
    )

    companion object {
        private val RULES: List<Rule> = listOf(
            Rule(
                regex = Regex(
                    """(?<!в\s)(\d+)\s*час(?:ов|а)?(?![а-яёa-z])""",
                    RegexOption.IGNORE_CASE
                ),
            ) { match ->
                match.groupValues[1].toIntOrNull()?.times(60)
            },
            Rule(
                regex = Regex("""(?<!в\s)(\d+)\s*ч(?![а-яёa-z])""", RegexOption.IGNORE_CASE),
            ) { match ->
                match.groupValues[1].toIntOrNull()?.times(60)
            },
            Rule(
                regex = Regex(
                    """(?<!в\s)(\d+)\s*мин(?:ут|уты|уту)?(?![а-яёa-z])""",
                    RegexOption.IGNORE_CASE
                ),
            ) { match ->
                match.groupValues[1].toIntOrNull()
            },
        )
    }
}