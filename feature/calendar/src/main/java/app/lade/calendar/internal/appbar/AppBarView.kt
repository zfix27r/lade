package app.lade.calendar.internal.appbar

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import app.lade.calendar.internal.domain.mode.CalendarMode
import app.lade.ui.profile.ProfileButton
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AppBarView(
    mode: CalendarMode,
    stripDate: LocalDate,
    onModeChange: (CalendarMode) -> Unit,
    onTitleClick: () -> Unit,
    onShare: () -> Unit,
    onOpenProfile: () -> Unit,
) {
    TopAppBar(
        title = {
            TextButton(onClick = onTitleClick) {
                AppBarTitle(stripDate = stripDate)
            }
        },
        actions = {
            AppBarModeBtn(mode = mode, onModeChange = onModeChange)
            AppBarMoreMenu(onShare = onShare)
            ProfileButton(onClick = onOpenProfile)
        },
    )
}