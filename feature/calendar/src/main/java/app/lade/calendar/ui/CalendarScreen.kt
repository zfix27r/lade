package app.lade.calendar.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.lade.calendar.domain.CalendarMode
import app.lade.calendar.ui.appbar.CalendarAppBar
import app.lade.calendar.ui.component.swipe.CalendarDateSwipe
import app.lade.calendar.ui.mode.CalendarModeActions
import app.lade.calendar.ui.mode.CalendarModeContent
import app.lade.draft.DraftHost
import app.lade.ui.share.captureScreen
import app.lade.ui.share.shareBitmap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    onEntryClick: (entryId: Long) -> Unit,
    onOpenProfile: () -> Unit,
    viewModel: CalendarViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var pickingDate by rememberSaveable { mutableStateOf(false) }
    val view = LocalView.current
    val context = LocalContext.current

    val actions = CalendarModeActions(
        onSwipe = viewModel::onSwipe,
        onDateSelected = viewModel::onDateSelected,
        onEditEntry = onEntryClick,
        onMarkDone = viewModel::markDone,
        onMarkSkip = viewModel::markSkip,
        onOpenDay = { date ->
            viewModel.onDateSelected(date)
            viewModel.onModeChange(CalendarMode.DAY)
        },
        onOpenMonth = { yearMonth ->
            viewModel.onDateSelected(yearMonth.atDay(1))
            viewModel.onModeChange(CalendarMode.MONTH)
        },
    )

    Scaffold(
        topBar = {
            CalendarAppBar(
                mode = state.mode,
                currentDate = state.currentDate,
                onModeChange = viewModel::onModeChange,
                view = state.view,
                onViewChange = viewModel::onViewChange,
                onTitleClick = viewModel::goToday,
                onShare = {
                    val bitmap = captureScreen(view)
                    shareBitmap(context, bitmap)
                },
                onOpenProfile = onOpenProfile,
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            CalendarDateSwipe(
                onSwipe = viewModel::onSwipe,
                modifier = Modifier.fillMaxSize(),
            ) {
                CalendarModeContent(
                    state = state,
                    actions = actions,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }

        DraftHost(
            defaultDate = state.currentDate,
            modifier = Modifier.fillMaxSize(),
        )
    }

    CalendarDatePickerDialog(
        visible = pickingDate,
        initialDate = state.currentDate,
        onDateSelected = { date -> viewModel.onDateSelected(date) },
        onDismiss = { pickingDate = false },
    )
}