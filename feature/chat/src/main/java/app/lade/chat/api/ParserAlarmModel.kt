package app.lade.chat.api

data class ParserAlarmModel(
    val time: String? = ParserContract.skip(),
    val date: String? = ParserContract.skip(),
)