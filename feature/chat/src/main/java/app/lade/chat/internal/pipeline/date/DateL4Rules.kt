package app.lade.chat.internal.pipeline.date

import app.lade.chat.api.FieldKey
import app.lade.chat.internal.state.ParseRule
import app.lade.chat.internal.state.ParseState
import app.lade.chat.internal.state.Priority
import app.lade.chat.api.RuleMatch
import app.lade.chat.api.RuleQuestion
import java.time.LocalDate

internal class DateL4Rules(
    private val today: LocalDate,
) : ParseRule {

    override val priority: Int = Priority.L4

    override fun match(raw: String, state: ParseState): List<RuleMatch> = emptyList()

    override fun question(raw: String, state: ParseState): List<RuleQuestion> {
        val result = mutableListOf<RuleQuestion>()
        for (rule in RULES) {
            val match = rule.regex.find(raw) ?: continue
            if (state.contains(rule.key)) continue
            if (result.any { it.key == rule.key }) continue
            result += RuleQuestion(
                key = rule.key,
                text = rule.question,
                answers = rule.answers,
                match = match.value,
                span = match.range,
            )
        }
        return result
    }

    private data class Rule(
        val key: FieldKey,
        val regex: Regex,
        val question: String,
        val answers: List<String>,
    )

    companion object {
        private val MONTH_PATTERN =
            "январ[ьяе]|феврал[ьяе]|март[ае]?|апрел[ьяе]|ма[йяе]|июн[ьяе]|июл[ьяе]|" +
                    "август[ае]?|сентябр[ьяе]|октябр[ьяе]|ноябр[ьяе]|декабр[ьяе]"

        private val RULES: List<Rule> = listOf(
            Rule(
                key = FieldKey.DATE_FROM,
                regex = Regex("""через\s+\d+\s+дн\w*""", RegexOption.IGNORE_CASE),
                question = "Через сколько дней?",
                answers = listOf("Сегодня", "Завтра", "Указать"),
            ),

            Rule(
                key = FieldKey.DATE_FROM,
                regex = Regex("""через\s+неделю""", RegexOption.IGNORE_CASE),
                question = "Через неделю?",
                answers = listOf("Да", "Нет", "Указать"),
            ),

            Rule(
                key = FieldKey.DATE_FROM,
                regex = Regex("""через\s+месяц""", RegexOption.IGNORE_CASE),
                question = "Через месяц?",
                answers = listOf("Да", "Нет", "Указать"),
            ),

            Rule(
                key = FieldKey.DATE_FROM,
                regex = Regex("""через\s+пару\s+дней""", RegexOption.IGNORE_CASE),
                question = "Через пару дней?",
                answers = listOf("Да", "Нет", "Указать"),
            ),

            Rule(
                key = FieldKey.DATE_FROM,
                regex = Regex("""на\s+следующей\s+неделе""", RegexOption.IGNORE_CASE),
                question = "На следующей неделе?",
                answers = listOf("Пн–вс", "Указать"),
            ),

            Rule(
                key = FieldKey.DATE_FROM,
                regex = Regex("""в\s+начале\s+($MONTH_PATTERN)""", RegexOption.IGNORE_CASE),
                question = "Начало месяца?",
                answers = listOf("1-е", "Указать"),
            ),

            Rule(
                key = FieldKey.DATE_FROM,
                regex = Regex("""в\s+конце\s+($MONTH_PATTERN)""", RegexOption.IGNORE_CASE),
                question = "Конец месяца?",
                answers = listOf("Последняя неделя", "Указать"),
            ),

            Rule(
                key = FieldKey.DATE_FROM,
                regex = Regex("""в\s+середине\s+($MONTH_PATTERN)""", RegexOption.IGNORE_CASE),
                question = "Середина месяца?",
                answers = listOf("15-е", "Указать"),
            ),

            Rule(
                key = FieldKey.DATE_FROM,
                regex = Regex("""в\s+($MONTH_PATTERN)""", RegexOption.IGNORE_CASE),
                question = "Весь месяц?",
                answers = listOf("Весь", "Указать"),
            ),

            Rule(
                key = FieldKey.DATE_FROM,
                regex = Regex("""\d{1,2}\.\d{1,2}\s+и\s+\d{1,2}\.\d{1,2}"""),
                question = "Два дня или повтор?",
                answers = listOf("Два дня", "Повтор"),
            ),
        )
    }
}