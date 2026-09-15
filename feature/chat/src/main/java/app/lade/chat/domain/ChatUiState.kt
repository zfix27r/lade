package app.lade.chat.domain

data class ChatUiState(
    val input: String = "",
    val bubbles: List<ChatBubble> = emptyList(),
    val sending: Boolean = false,
    val choices: List<ChatChoiceOption> = emptyList(),
    val choicePromptKey: String? = null,
)
