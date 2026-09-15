package app.lade.chat.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import app.lade.chat.domain.ChatBubble
import app.lade.resources.R

@Composable
fun bubbleText(bubble: ChatBubble): String {
    if (bubble.text.isNotBlank()) return bubble.text
    return when (bubble.messageKey) {
        "hint" -> stringResource(R.string.chat_hint_examples)
        "empty" -> stringResource(R.string.chat_err_empty)
        "no_template" -> stringResource(R.string.chat_err_no_template)
        "bad_duration" -> stringResource(R.string.chat_err_bad_duration)
        "habit_not_found" -> stringResource(R.string.chat_err_habit_not_found)
        "habit_missing" -> stringResource(R.string.chat_err_habit_missing)
        "choice_which_entry" -> stringResource(R.string.chat_choice_prompt)
        "no_category" -> stringResource(R.string.chat_err_no_category)
        "block_overlap" -> stringResource(R.string.chat_err_block_overlap)
        "habit" -> stringResource(R.string.chat_ok_habit)
        "block" -> stringResource(R.string.chat_ok_block)
        "task" -> stringResource(R.string.chat_ok_task)
        else -> stringResource(R.string.chat_err_parse)
    }
}