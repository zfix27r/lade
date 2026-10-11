package app.lade.parser.internal.match

internal data class ParserMatchNeedle(
    val text: String,
    val intent: String?,
) {
    val stem: String get() = text.removePrefix("*").removeSuffix("*")
}