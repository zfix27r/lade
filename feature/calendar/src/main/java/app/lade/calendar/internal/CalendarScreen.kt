package app.lade.calendar.internal

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberBottomSheetState
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
import app.lade.agendaui.internal.AgendaDetailScreen
import app.lade.calendar.api.config.CalendarConfig
import app.lade.calendar.api.config.DefaultCalendarConfig
import app.lade.calendar.internal.appbar.AppBarView
import app.lade.calendar.internal.main.CalendarMode
import app.lade.calendar.internal.main.CalendarModeActions
import app.lade.calendar.internal.main.CalendarModeContent
import app.lade.calendar.internal.main.CalendarUiEvent
import app.lade.calendardata.api.agenda.CalendarDataAgendaModel
import app.lade.draft.api.DraftApi
import app.lade.draft.api.DraftHost
import app.lade.draft.api.DraftPhase
import app.lade.ui.scrim.ScrimHost
import app.lade.ui.share.captureScreen
import app.lade.ui.share.shareBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CalendarScreen(
    onOpenProfile: () -> Unit,
    draftApi: DraftApi,
    modifier: Modifier = Modifier,
    config: CalendarConfig = DefaultCalendarConfig,
    viewModel: CalendarViewModel = hiltViewModel(),
) {
    var selectedEntry by remember { mutableStateOf<Pair<LocalDate, CalendarDataAgendaModel>?>(null) }
    var openedEntry by remember { mutableStateOf<Pair<LocalDate, CalendarDataAgendaModel>?>(null) }
    val view = LocalView.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val sheetState = rememberBottomSheetState(
        initialValue = SheetValue.Hidden,
    )

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
            onVisibleDateChange = { date -> viewModel.onVisibleDateChange(date) },
            onEditEntry = { entryId -> draftApi.open(entryId) },
            onOpenAgenda = { date, agenda -> openedEntry = date to agenda },
            onEntryLongPress = { date, agenda -> selectedEntry = date to agenda },
            onToggleDone = viewModel::toggleDone,
            onGoalToggle = viewModel::toggleGoal,
            onGoalValueChange = viewModel::setGoalAmount,
            onStartTimer = viewModel::startTimer,
            onFinishTimer = viewModel::finishTimer,
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
                val (date, agenda) = opened
                AgendaDetailScreen(
                    entryId = agenda.entry.id,
                    date = date,
                    onBack = { openedEntry = null },
                    onEdit = {
                        openedEntry = null
                        draftApi.open(agenda.entry.id)
                    },
                    onDelete = {
                        openedEntry = null
                        viewModel.archiveEntry(agenda.entry.id)
                    },
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    topBar = {
                        AppBarView(
                            mode = viewModel.mode,
                            currentDate = viewModel.selectedDate,
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
                            mode = viewModel.mode,
                            selectedDate = viewModel.selectedDate,
                            visibleDate = viewModel.visibleDate,
                            weeks = viewModel.weeks,
                            days = viewModel.days,
                            marks = viewModel.marks,
                            actions = actions,
                            config = config,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }
            }
        }

        selectedEntry?.let { (_, agenda) ->
            key(agenda.entry.id) {
                ModalBottomSheet(
                    onDismissRequest = { selectedEntry = null },
                    sheetState = sheetState,
                    scrimColor = Color.Transparent,
                ) {
                    EntryActionsSheet(
                        title = agenda.entry.title,
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
        defaultDate = viewModel.selectedDate,
        modifier = Modifier.fillMaxSize(),
    )
}