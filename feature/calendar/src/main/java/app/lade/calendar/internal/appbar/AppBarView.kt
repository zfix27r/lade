package app.lade.calendar.internal.appbar

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.lade.calendar.internal.main.CalendarMode
import app.lade.ui.profile.ProfileButton
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AppBarView(
    mode: StateFlow<CalendarMode>,
    currentDate: StateFlow<LocalDate>,
    onModeChange: (CalendarMode) -> Unit,
    onTitleClick: () -> Unit,
    onShare: () -> Unit,
    onOpenProfile: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = {
            TextButton(onClick = onTitleClick) {
                AppBarTitle(
                    currentDate = currentDate,
                    modifier = modifier
                )
            }
        },
        actions = {
            AppBarModeBtn(mode = mode, onModeChange = onModeChange)
            AppBarMoreMenu(onShare = onShare)
            ProfileButton(onClick = onOpenProfile)
        },
    )
}