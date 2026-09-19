package app.lade.chat.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import app.lade.chat.domain.ChatBubble
import app.lade.chat.domain.ChatBubbleKind
import app.lade.entrykind.EntryKind
import app.lade.resources.R

@Composable
fun ChatBubbleRow(
    bubble: ChatBubble,
    onCreateEntry: (kind: String, title: String) -> Unit,
) {
    val align = if (bubble.kind == ChatBubbleKind.USER) Alignment.CenterEnd else Alignment.CenterStart
    val bg = when (bubble.kind) {
        ChatBubbleKind.USER -> MaterialTheme.colorScheme.primaryContainer
        ChatBubbleKind.OK -> MaterialTheme.colorScheme.secondaryContainer
        ChatBubbleKind.ERROR -> MaterialTheme.colorScheme.errorContainer
        ChatBubbleKind.HINT -> MaterialTheme.colorScheme.surfaceVariant
    }
    val fg = when (bubble.kind) {
        ChatBubbleKind.USER -> MaterialTheme.colorScheme.onPrimaryContainer
        ChatBubbleKind.OK -> MaterialTheme.colorScheme.onSecondaryContainer
        ChatBubbleKind.ERROR -> MaterialTheme.colorScheme.onErrorContainer
        ChatBubbleKind.HINT -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = align) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .background(bg, RoundedCornerShape(dimensionResource(R.dimen.spacing_md)))
                .padding(dimensionResource(R.dimen.spacing_md)),
        ) {
            Text(
                text = bubbleText(bubble),
                style = MaterialTheme.typography.bodyLarge,
                color = fg,
            )
            bubble.detail?.takeIf { it.isNotBlank() }?.let { detail ->
                Text(
                    text = detail,
                    style = MaterialTheme.typography.bodyMedium,
                    color = fg,
                    modifier = Modifier.padding(top = dimensionResource(R.dimen.spacing_xs)),
                )
            }
            val createTitle = bubble.createTitle
            if (bubble.messageKey == "habit_missing" && !createTitle.isNullOrBlank()) {
                val kind = bubble.createKind ?: EntryKind.HABIT.storage
                TextButton(onClick = { onCreateEntry(kind, createTitle) }) {
                    Text(stringResource(R.string.chat_create_habit, createTitle))
                }
            }
        }
    }
}