package app.lade.chat.api

data class ParserReminderModel(
    val date: String? = ParserContract.skip(),
    val time: String? = ParserContract.skip(),
)