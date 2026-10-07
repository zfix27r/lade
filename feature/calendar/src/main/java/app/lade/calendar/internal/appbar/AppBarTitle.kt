package app.lade.calendar.internal.appbar

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import app.lade.calendar.internal.appbar.data.appBarTitleText
import app.lade.ui.theme.LocalAppLocale
import app.lade.ui.theme.LocalToday
import java.time.LocalDate

@Composable
internal fun AppBarTitle(
    stripDate: LocalDate,
    modifier: Modifier = Modifier,
) {
    val locale = LocalAppLocale.current
    val today = LocalToday.current

    val text = remember(stripDate, today, locale) {
        appBarTitleText(
            date = stripDate,
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