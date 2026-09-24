package app.lade.chat.api

data class ParsedField(
    val key: FieldKey,
    val value: FieldValue,
    val match: String,
)