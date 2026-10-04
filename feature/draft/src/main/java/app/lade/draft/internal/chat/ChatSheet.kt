package app.lade.draft.internal.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import app.lade.draft.internal.chat.chip.ChipView

@Composable
internal fun BarChatSheet(
    viewModel: ChatViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        ChipView()
    }
}