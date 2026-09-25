package app.lade.draft.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.lade.draft.internal.ui.DraftBar
import java.time.LocalDate

@Composable
fun DraftHost(
    defaultDate: LocalDate?,
    modifier: Modifier = Modifier,
) {
    DraftBar(
        defaultDate = defaultDate,
        modifier = modifier,
    )
}