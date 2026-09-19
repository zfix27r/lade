package app.lade.draft

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.lade.draft.internal.host.InputBarHostContent
import app.lade.draft.internal.host.InputBarHostViewModel
import java.time.LocalDate

@Composable
fun DraftHost(
    defaultDate: LocalDate?,
    modifier: Modifier = Modifier,
) {
    val viewModel: InputBarHostViewModel = hiltViewModel()
    val draft by viewModel.draft.collectAsStateWithLifecycle()

    BackHandler(enabled = !draft.isEmpty) {
        viewModel.onCancel()
    }

    Box(modifier = modifier.fillMaxSize()) {
        InputBarHostContent(
            defaultDate = defaultDate,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}