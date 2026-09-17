package app.lade.calendar.ui.appbar

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.text.style.TextOverflow
import app.lade.calendar.domain.CalendarMode
import app.lade.calendar.domain.CalendarView
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarAppBar(
    mode: CalendarMode,
    currentDate: LocalDate,
    onModeChange: (CalendarMode) -> Unit,
    view: CalendarView,
    onViewChange: (CalendarView) -> Unit,
    onTitleClick: () -> Unit,
    onShare: () -> Unit,
) {
    val locale = LocalLocale.current.platformLocale
    val title = calendarTitle(mode, currentDate, locale)

    TopAppBar(
        title = {
            TextButton(onClick = onTitleClick) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        },
        actions = {
            AppBarModeBtn(mode = mode, onModeChange = onModeChange)
            if (mode == CalendarMode.LIST) {
                AppBarViewBtn(view = view, onViewChange = onViewChange)
            }
            AppBarMoreMenu(onShare = onShare)
        },
    )
}