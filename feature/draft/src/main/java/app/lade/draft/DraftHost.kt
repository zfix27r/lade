package app.lade.draft

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import app.lade.draft.internal.DraftBar
import java.time.LocalDate

@Composable
fun DraftHost(
    defaultDate: LocalDate?,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier.fillMaxSize()) {
        content()
        DraftBar(
            defaultDate = defaultDate,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}