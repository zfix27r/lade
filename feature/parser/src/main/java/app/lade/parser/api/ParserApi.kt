package app.lade.parser.api

interface ParserApi {
    suspend fun parse(model: ParserModel): ParserModel
}