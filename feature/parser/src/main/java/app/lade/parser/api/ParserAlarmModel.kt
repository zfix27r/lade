package app.lade.parser.api

data class ParserAlarmModel(
    val time: String? = ParserContract.skip(),
    val date: String? = ParserContract.skip(),
)