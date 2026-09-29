package app.lade.chat.api

interface ChatApi {
    suspend fun parse(model: ParserModel): ParserModel
}