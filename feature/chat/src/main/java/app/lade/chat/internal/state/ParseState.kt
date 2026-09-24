package app.lade.chat.internal.state

import app.lade.chat.api.FieldKey
import app.lade.chat.api.ParsedField

internal data class ParseState(
    val fields: List<ParsedField> = emptyList(),
) {
    fun add(field: ParsedField): ParseState =
        copy(fields = fields + field)

    fun remove(key: FieldKey): ParseState =
        copy(fields = fields.filterNot { it.key == key })

    fun contains(key: FieldKey): Boolean =
        fields.any { it.key == key }

    fun get(key: FieldKey): ParsedField? =
        fields.firstOrNull { it.key == key }
}