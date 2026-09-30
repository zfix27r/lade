package app.lade.draft.internal.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import app.lade.draft.internal.chat.chip.ChipView

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun BarChatSheet(
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = hiltViewModel(),
) {
    Column(modifier = modifier.fillMaxWidth()) {
        ChipView()
    }
}