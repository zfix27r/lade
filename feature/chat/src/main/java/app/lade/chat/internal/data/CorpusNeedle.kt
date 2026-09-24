package app.lade.chat.internal.data

internal data class CorpusNeedle(
    val text: String,
    val intent: String?,
) {
    val stem: String get() = text.removePrefix("*").removeSuffix("*")
}