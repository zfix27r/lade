package app.lade.chat.api

import app.lade.draftdata.DraftModel

interface ChatApi {
    suspend fun parse(raw: String, draft: DraftModel): ParseResult
}