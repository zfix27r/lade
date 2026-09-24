package app.lade.chat

data class PhraseCase(
    val input: String,
    val result: String,
    val kind: String? = null,
    val title: String? = null,
    val rrule: String? = null,
    val dateFrom: String? = null,
    val dateTo: String? = null,
    val timeFrom: String? = null,
    val timeEnd: String? = null,
    val goals: String? = null,
    val remaining: String? = null,
)