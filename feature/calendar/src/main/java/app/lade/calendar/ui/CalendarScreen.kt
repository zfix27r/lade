package app.lade.calendar.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.lade.calendar.domain.CalendarMode
import app.lade.calendar.ui.appbar.CalendarAppBar
import app.lade.calendar.ui.component.list.CalendarListLayout
import app.lade.calendar.ui.component.month.CalendarMonthGrid
import app.lade.calendar.ui.component.swipe.CalendarDateSwipe
import app.lade.calendar.ui.component.week.CalendarWeekLayout
import app.lade.calendar.ui.component.year.CalendarYearGrid
import app.lade.resources.R
import app.lade.ui.share.captureScreen
import app.lade.ui.share.shareBitmap
import java.time.Instant
import java.time.YearMonth
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    onEntryClick: (entryId: Long) -> Unit,
    onCreateEntry: (kind: String, dateEpochDay: Long) -> Unit,
    viewModel: CalendarViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var pickingDate by rememberSaveable { mutableStateOf(false) }
    var showCreateSheet by rememberSaveable { mutableStateOf(false) }
    val view = LocalView.current
    val context = LocalContext.current

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
            )
        },
        floatingActionButton = {
            if (state.mode != CalendarMode.YEAR) {
                FloatingActionButton(onClick = { showCreateSheet = true }) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = stringResource(R.string.action_add),
                    )
                }
            }
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
                when (state.mode) {
                    CalendarMode.LIST -> CalendarListLayout(
                        state = state,
                        onSwipe = viewModel::onSwipe,
                        onDateSelected = viewModel::onDateSelected,
                        onEditEntry = onEntryClick,
                        onMarkDone = viewModel::markDone,
                        onMarkSkip = viewModel::markSkip,
                        modifier = Modifier.fillMaxSize(),
                    )

                    CalendarMode.DAY,
                    CalendarMode.DAY_3,
                    CalendarMode.WEEK,
                        -> CalendarWeekLayout(
                        state = state,
                        onSwipe = viewModel::onSwipe,
                        onDateSelected = viewModel::onDateSelected,
                        onEditEntry = onEntryClick,
                        modifier = Modifier.fillMaxSize(),
                    )

                    CalendarMode.MONTH -> CalendarMonthGrid(
                        month = YearMonth.from(state.currentDate),
                        entries = state.entries,
                        selectedDate = state.currentDate,
                        onSwipe = viewModel::onSwipe,
                        onOpenDay = { date ->
                            viewModel.onDateSelected(date)
                            viewModel.onModeChange(CalendarMode.DAY)
                        },
                        modifier = Modifier.fillMaxSize(),
                    )

                    CalendarMode.YEAR -> CalendarYearGrid(
                        year = state.currentDate.year,
                        entries = state.entries,
                        onSwipe = viewModel::onSwipe,
                        onOpenMonth = { yearMonth ->
                            viewModel.onDateSelected(yearMonth.atDay(1))
                            viewModel.onModeChange(CalendarMode.MONTH)
                        },
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }

    if (pickingDate) {
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = state.currentDate.atStartOfDay(ZoneOffset.UTC).toInstant()
                .toEpochMilli(),
        )
        DatePickerDialog(
            onDismissRequest = { pickingDate = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        pickerState.selectedDateMillis?.let { millis ->
                            viewModel.onDateSelected(
                                Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate(),
                            )
                        }
                        pickingDate = false
                    },
                ) { Text(stringResource(R.string.action_ok)) }
            },
            dismissButton = {
                TextButton(onClick = { pickingDate = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            },
        ) {
            DatePicker(state = pickerState)
        }
    }

    if (showCreateSheet) {
        CalendarCreateSheet(
            date = state.currentDate,
            onDismiss = { showCreateSheet = false },
            onCreate = { kind ->
                onCreateEntry(kind.storage, state.currentDate.toEpochDay())
            },
        )
    }
}