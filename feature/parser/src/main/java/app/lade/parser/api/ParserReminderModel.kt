package app.lade.parser.api

data class ParserReminderModel(
    val date: String? = ParserContract.skip(),
    val time: String? = ParserContract.skip(),
)