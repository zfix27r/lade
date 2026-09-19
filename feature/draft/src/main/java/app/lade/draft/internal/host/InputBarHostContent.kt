package app.lade.draft.internal.host

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.lade.draft.internal.bar.InputBar
import app.lade.draft.internal.bar.InputBarState
import app.lade.draft.internal.bar.conf.DraftBarConfSheet
import app.lade.draft.internal.bar.goal.DraftBarGoalSheet
import java.time.LocalDate

@Composable
internal fun InputBarHostContent(
    defaultDate: LocalDate?,
    modifier: Modifier = Modifier,
) {
    val viewModel: InputBarHostViewModel = hiltViewModel()
    val draft by viewModel.draft.collectAsStateWithLifecycle()
    val state = remember { InputBarState() }
    var confVisible by remember { mutableStateOf(false) }
    var goalVisible by remember { mutableStateOf(false) }

    BackHandler(enabled = !draft.isEmpty) {
        viewModel.onCancel()
    }

    LaunchedEffect(defaultDate) {
        viewModel.setDefaultDate(defaultDate)
    }

    LaunchedEffect(draft.title) {
        if (state.text != draft.title) {
            state.onTextChange(draft.title)
        }
    }

    InputBar(
        state = state,
        draft = draft,
        placeholder = "Новая запись…",
        onTextChange = viewModel::onTextChange,
        onSubmit = viewModel::onSubmit,
        onExpand = viewModel::onExpand,
        onConf = { confVisible = true },
        onGoal = { goalVisible = true },
        modifier = modifier,
    )

    if (confVisible) {
        DraftBarConfSheet(onDismiss = { confVisible = false })
    }

    if (goalVisible) {
        DraftBarGoalSheet(onDismiss = { goalVisible = false })
    }
}