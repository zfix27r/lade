package app.lade.chat.api

internal data class RuleMatch(
    val key: FieldKey,
    val value: FieldValue,
    val match: String,
    val span: IntRange,
)