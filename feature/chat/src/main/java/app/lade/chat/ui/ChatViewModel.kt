package app.lade.chat.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lade.chat.domain.ApplyChatCommand
import app.lade.chat.domain.ApplyResult
import app.lade.chat.domain.ChatBubble
import app.lade.chat.domain.ChatBubbleKind
import app.lade.chat.domain.ChatChoiceOption
import app.lade.chat.domain.ChatCommand
import app.lade.chat.domain.ChatHistoryRepository
import app.lade.chat.domain.ChatUiState
import app.lade.chat.domain.model.ChatMessage
import app.lade.chat.domain.pipeline.ChatParseOrchestrator
import app.lade.chat.domain.pipeline.ParseOutcome
import app.lade.agenda.api.entry.EntryKind
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ChatViewModel @Inject constructor(
	private val orchestrator: ChatParseOrchestrator,
	private val applyChatCommand: ApplyChatCommand,
	private val historyRepository: ChatHistoryRepository,
) : ViewModel() {
	private val _state = MutableStateFlow(ChatUiState())
	val state: StateFlow<ChatUiState> = _state.asStateFlow()

	init {
		viewModelScope.launch {
			var seededHint = false
			historyRepository.observeAll().collect { stored ->
				if (stored.isEmpty() && !seededHint && !_state.value.sending) {
					seededHint = true
					historyRepository.append(
						ChatMessage(
							kind = ChatMessage.KIND_HINT,
							messageKey = "hint",
							createdAtEpochMs = System.currentTimeMillis(),
						),
					)
					return@collect
				}
				_state.update {
					it.copy(bubbles = stored.map { msg -> msg.toBubble() })
				}
			}
		}
	}

	fun onInputChange(value: String) = _state.update { it.copy(input = value) }

	fun send() {
		val text = _state.value.input.trim()
		if (text.isEmpty() || _state.value.sending) return
		viewModelScope.launch {
			_state.update {
				it.copy(sending = true, input = "", choices = emptyList(), choicePromptKey = null)
			}
			historyRepository.append(
				ChatMessage(
					kind = ChatMessage.KIND_USER,
					text = text,
					createdAtEpochMs = System.currentTimeMillis(),
				),
			)
			handleOutcome(orchestrator.run(text))
			_state.update { it.copy(sending = false) }
		}
	}

	fun onChoice(option: ChatChoiceOption) {
		viewModelScope.launch {
			_state.update { it.copy(choices = emptyList(), choicePromptKey = null) }
			historyRepository.append(
				ChatMessage(
					kind = ChatMessage.KIND_USER,
					text = option.label,
					createdAtEpochMs = System.currentTimeMillis(),
				),
			)
			applyAndPersist(option.command)
		}
	}

	fun dismissChoices() {
		_state.update { it.copy(choices = emptyList(), choicePromptKey = null) }
	}

	private suspend fun handleOutcome(result: ParseOutcome) {
		when (result) {
			is ParseOutcome.Execute -> applyAndPersist(result.command)
			is ParseOutcome.ChooseEntry -> {
				historyRepository.append(
					ChatMessage(
						kind = ChatMessage.KIND_HINT,
						messageKey = result.promptKey,
						createdAtEpochMs = System.currentTimeMillis(),
					),
				)
				_state.update {
					it.copy(
						choices = result.options.map { pick ->
							ChatChoiceOption(label = pick.label, command = pick.command)
						},
						choicePromptKey = result.promptKey,
					)
				}
			}
			is ParseOutcome.SuggestCreate -> {
				historyRepository.append(
					ChatMessage(
						kind = ChatMessage.KIND_ERROR,
						messageKey = "habit_missing",
						detail = "${result.draft.kind.storage}|${result.draft.title}",
						createdAtEpochMs = System.currentTimeMillis(),
					),
				)
			}
			is ParseOutcome.Failed -> {
				historyRepository.append(
					ChatMessage(
						kind = ChatMessage.KIND_ERROR,
						messageKey = result.reason,
						createdAtEpochMs = System.currentTimeMillis(),
					),
				)
			}
		}
	}

	private suspend fun applyAndPersist(command: ChatCommand) {
		when (val applied = applyChatCommand.apply(command)) {
			is ApplyResult.Ok -> historyRepository.append(
				ChatMessage(
					kind = ChatMessage.KIND_OK,
					messageKey = applied.messageKey,
					detail = applied.detail,
					createdAtEpochMs = System.currentTimeMillis(),
				),
			)
			is ApplyResult.Err -> historyRepository.append(
				ChatMessage(
					kind = ChatMessage.KIND_ERROR,
					messageKey = applied.messageKey,
					detail = listOfNotNull(applied.createKind, applied.createTitle)
						.joinToString("|")
						.ifBlank { applied.createTitle },
					createdAtEpochMs = System.currentTimeMillis(),
				),
			)
		}
	}
}

private fun ChatMessage.toBubble(): ChatBubble {
	val parts = detail?.split("|").orEmpty()
	val createKind = parts.getOrNull(0)?.takeIf {
		it in EntryKind.entries.map { k -> k.storage }
	}
	val createTitle = when {
		messageKey == "habit_missing" && parts.size >= 2 -> parts[1]
		messageKey == "habit_missing" -> detail
		else -> null
	}
	return ChatBubble(
		id = id,
		kind = when (kind) {
			ChatMessage.KIND_USER -> ChatBubbleKind.USER
			ChatMessage.KIND_OK -> ChatBubbleKind.OK
			ChatMessage.KIND_ERROR -> ChatBubbleKind.ERROR
			else -> ChatBubbleKind.HINT
		},
		text = text,
		messageKey = messageKey,
		detail = if (messageKey == "habit_missing" || messageKey == "choice_which_entry") {
			null
		} else {
			detail
		},
		createTitle = createTitle,
		createKind = createKind ?: if (messageKey == "habit_missing") EntryKind.HABIT.storage else null,
	)
}
