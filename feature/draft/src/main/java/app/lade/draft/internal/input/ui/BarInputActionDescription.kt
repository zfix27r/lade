package app.lade.draft.internal.input.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import app.lade.draft.R
import app.lade.draft.internal.input.domain.BarInputAction

@Composable
internal fun BarInputAction.description(): String = when (this) {
    BarInputAction.Commit -> stringResource(R.string.draft_action_commit)
    BarInputAction.Send -> stringResource(R.string.draft_action_save)
}