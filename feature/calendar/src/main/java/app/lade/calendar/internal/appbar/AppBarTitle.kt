package app.lade.calendar.internal.appbar

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.lade.calendar.internal.appbar.data.appBarTitleText
import app.lade.ui.theme.LocalAppLocale
import app.lade.ui.theme.LocalToday
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate

@Composable
internal fun AppBarTitle(
    currentDate: StateFlow<LocalDate>,
    modifier: Modifier = Modifier,
) {
    val locale = LocalAppLocale.current
    val today = LocalToday.current
    val currentDateState by currentDate.collectAsStateWithLifecycle()

    val text = remember(currentDateState, today, locale) {
        appBarTitleText(
            date = currentDateState,
            today = today,
            locale = locale,
        )
    }

    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onSurface,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier,
    )
}