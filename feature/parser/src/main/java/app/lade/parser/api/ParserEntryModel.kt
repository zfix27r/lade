package app.lade.parser.api

data class ParserEntryModel(
    val kind: String? = ParserContract.skip(),
    val title: String? = ParserContract.skip(),
    val dateFrom: String? = ParserContract.skip(),
    val dateTo: String? = ParserContract.skip(),
    val timeFrom: String? = ParserContract.skip(),
    val timeTo: String? = ParserContract.skip(),
    val durationMinutes: String? = ParserContract.skip(),
    val rrule: String? = ParserContract.skip(),
)