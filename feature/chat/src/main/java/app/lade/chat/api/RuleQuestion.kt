package app.lade.chat.api

data class RuleQuestion(
    val key: FieldKey,
    val text: String,
    val answers: List<String>,
    val match: String,
    val span: IntRange,
)