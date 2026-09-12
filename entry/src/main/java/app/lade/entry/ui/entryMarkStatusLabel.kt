package app.lade.entry.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import app.lade.entry.data.EntryHistoryResult
import app.lade.resources.R


@Composable
fun entryMarkStatusLabel(result: String?): String? = when (result) {
    EntryHistoryResult.DONE -> stringResource(R.string.habit_status_done)
    EntryHistoryResult.SKIPPED -> stringResource(R.string.habit_status_skipped)
    else -> null
}