package app.lade.chat.domain

data class ChatBubble(
    val id: Long,
    val kind: ChatBubbleKind,
    val text: String,
    val messageKey: String? = null,
    val detail: String? = null,
    val createTitle: String? = null,
    val createKind: String? = null,
)