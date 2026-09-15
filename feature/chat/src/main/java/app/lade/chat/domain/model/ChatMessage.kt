package app.lade.chat.domain.model

data class ChatMessage(
	val id: Long = 0,
	val kind: String,
	val text: String = "",
	val messageKey: String? = null,
	val detail: String? = null,
	val createdAtEpochMs: Long = 0,
) {
	companion object {
		const val KIND_USER = "USER"
		const val KIND_OK = "OK"
		const val KIND_ERROR = "ERROR"
		const val KIND_HINT = "HINT"
		/** Not persisted (v3 choice-ephemeral). */
		const val KIND_CHOICE = "CHOICE"
	}
}
