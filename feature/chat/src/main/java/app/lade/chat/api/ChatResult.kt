package app.lade.chat.api

import app.lade.draftdata.DraftModel

sealed interface ChatResult {
    data class Draft(val draft: DraftModel) : ChatResult
    data class MarkDone(val systemKey: String) : ChatResult
    data object Unclear : ChatResult
}