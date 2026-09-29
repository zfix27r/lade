package app.lade.calendar.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.lade.agenda.api.agenda.AgendaModel
import app.lade.agendaui.internal.AgendaDetailScreen
import app.lade.calendar.domain.CalendarMode
import app.lade.calendar.ui.appbar.CalendarAppBar
import app.lade.calendar.ui.component.swipe.CalendarDateSwipe
import app.lade.calendar.ui.mode.CalendarModeActions
import app.lade.calendar.ui.mode.CalendarModeContent
import app.lade.draft.api.DraftApi
import app.lade.draft.api.DraftHost
import app.lade.draft.api.DraftPhase
import app.lade.ui.scrim.ScrimHost
import app.lade.ui.share.captureScreen
import app.lade.ui.share.shareBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    onOpenProfile: () -> Unit,
    draftApi: DraftApi,
    viewModel: CalendarViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var selectedEntry by remember { mutableStateOf<AgendaModel?>(null) }
    var openedAgenda by remember { mutableStateOf<AgendaModel?>(null) }
    val view = LocalView.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val sheetState = rememberModalBottomSheetState()

    val draftPhase by draftApi.phase.collectAsStateWithLifecycle()
    val draftIsEditing = draftPhase == DraftPhase.EDIT

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is CalendarUiEvent.Error -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    val actions = remember(viewModel) {
        CalendarModeActions(
            onSwipe = viewModel::onSwipe,
            onDateSelected = viewModel::onDateSelected,
            onEditEntry = { entryId -> draftApi.open(entryId) },
            onOpenAgenda = { agenda -> openedAgenda = agenda },
            onEntryLongPress = { agenda -> selectedEntry = agenda },
            onToggleDone = viewModel::toggleDone,
            onOpenDay = { date ->
                viewModel.onDateSelected(date)
                viewModel.onModeChange(CalendarMode.DAY)
            },
            onOpenMonth = { yearMonth ->
                viewModel.onDateSelected(yearMonth.atDay(1))
                viewModel.onModeChange(CalendarMode.MONTH)
            },
        )
    }

    BackHandler(enabled = openedAgenda != null) { openedAgenda = null }

    Box(Modifier.fillMaxSize()) {
        ScrimHost(
            isVisible = selectedEntry != null || draftIsEditing,
        ) {
            val opened = openedAgenda
            if (opened != null) {
                AgendaDetailScreen(
                    entryId = opened.entry.id,
                    date = opened.date,
                    onBack = { openedAgenda = null },
                    onEdit = {
                        openedAgenda = null
                        draftApi.open(opened.entry.id)
                    },
                    onDelete = {
                        openedAgenda = null
                        viewModel.archiveEntry(opened.entry.id)
                    },
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    topBar = {
                        CalendarAppBar(
                            mode = state.mode,
                            currentDate = state.currentDate,
                            onModeChange = viewModel::onModeChange,
                            view = state.view,
                            onViewChange = viewModel::onViewChange,
                            onTitleClick = viewModel::goToday,
                            onShare = {
                                scope.launch {
                                    val bitmap = withContext(Dispatchers.Default) {
                                        captureScreen(view)
                                    }
                                    shareBitmap(context, bitmap)
                                }
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
                }
            }
        }

        selectedEntry?.let { agenda ->
            key(agenda.entry.id) {
                ModalBottomSheet(
                    onDismissRequest = { selectedEntry = null },
                    sheetState = sheetState,
                    scrimColor = Color.Transparent,
                ) {
                    EntryActionsSheet(
                        agenda = agenda,
                        onEdit = {
                            selectedEntry = null
                            draftApi.open(agenda.entry.id)
                        },
                        onDelete = {
                            selectedEntry = null
                            viewModel.archiveEntry(agenda.entry.id)
                        },
                    )
                }
            }
        }
    }

    DraftHost(
        defaultDate = state.currentDate,
        modifier = Modifier.fillMaxSize(),
    )
}