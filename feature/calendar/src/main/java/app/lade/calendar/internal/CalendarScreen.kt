package app.lade.calendar.internal

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
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
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
import app.lade.agendaui.internal.AgendaDetailScreen
import app.lade.calendar.api.config.CalendarConfig
import app.lade.calendar.api.config.DefaultCalendarConfig
import app.lade.calendar.internal.appbar.AppBarView
import app.lade.calendar.internal.domain.CalendarUiEvent
import app.lade.calendar.internal.domain.mode.CalendarMode
import app.lade.calendar.internal.domain.mode.CalendarModeActions
import app.lade.calendar.internal.ui.EntryActionsSheet
import app.lade.calendar.internal.ui.mode.CalendarModeContent
import app.lade.calendardata.api.CalendarCardModel
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
internal fun CalendarScreen(
    onOpenProfile: () -> Unit,
    draftApi: DraftApi,
    config: CalendarConfig = DefaultCalendarConfig,
    modifier: Modifier = Modifier,
    viewModel: CalendarViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val stripState by viewModel.strip.state.collectAsStateWithLifecycle()
    var selectedEntry by remember { mutableStateOf<CalendarCardModel?>(null) }
    var openedEntry by remember { mutableStateOf<CalendarCardModel?>(null) }
    val view = LocalView.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val sheetState = rememberModalBottomSheetState()

    val draftPhase by draftApi.phase.collectAsStateWithLifecycle()
    val draftIsEditing = draftPhase == DraftPhase.EDIT

    val offsetXState: State<Float> = viewModel.strip.state
        .collectAsState(initial = stripState)
        .let { stateFlowState ->
            remember(stateFlowState) {
                derivedStateOf { stateFlowState.value.offsetX }
            }
        }

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
            onOpenAgenda = { card -> openedEntry = card },
            onEntryLongPress = { card -> selectedEntry = card },
            onToggleDone = viewModel::toggleDone,
            onGoalToggle = viewModel::toggleGoal,
            onOpenDay = { date ->
                viewModel.onDateSelected(date)
                viewModel.onModeChange(CalendarMode.WEEK)
            },
            onOpenMonth = { yearMonth ->
                viewModel.onDateSelected(yearMonth.atDay(1))
                viewModel.onModeChange(CalendarMode.MONTH)
            },
            onTimelineScroll = viewModel::onTimelineScroll,
        )
    }

    BackHandler(enabled = openedEntry != null) { openedEntry = null }

    Box(modifier.fillMaxSize()) {
        ScrimHost(isVisible = selectedEntry != null || draftIsEditing) {
            val opened = openedEntry
            if (opened != null) {
                AgendaDetailScreen(
                    entryId = opened.entryId,
                    date = opened.date,
                    onBack = { openedEntry = null },
                    onEdit = {
                        openedEntry = null
                        draftApi.open(opened.entryId)
                    },
                    onDelete = {
                        openedEntry = null
                        viewModel.archiveEntry(opened.entryId)
                    },
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    topBar = {
                        AppBarView(
                            mode = state.mode,
                            stripDate = stripState.date,
                            onModeChange = viewModel::onModeChange,
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
                        CalendarModeContent(
                            state = state,
                            stripState = stripState,
                            strip = viewModel.strip,
                            offsetXState = offsetXState,
                            actions = actions,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }
            }
        }

        selectedEntry?.let { card ->
            key(card.entryId) {
                ModalBottomSheet(
                    onDismissRequest = { selectedEntry = null },
                    sheetState = sheetState,
                    scrimColor = Color.Transparent,
                ) {
                    EntryActionsSheet(
                        title = card.title,
                        onEdit = {
                            selectedEntry = null
                            draftApi.open(card.entryId)
                        },
                        onDelete = {
                            selectedEntry = null
                            viewModel.archiveEntry(card.entryId)
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