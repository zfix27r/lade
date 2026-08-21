package app.lade.calendar.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.lade.R
import app.lade.habits.domain.model.HabitHistoryResult
import app.lade.time.domain.model.TimeBlockSource
import java.time.LocalDate
import java.time.YearMonth

enum class CalendarView(
	@param:StringRes val titleRes: Int,
) {
	Day(R.string.calendar_view_day),
	Feed(R.string.calendar_view_feed),
	Month(R.string.calendar_view_month),
	Week(R.string.calendar_view_week),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
	onBack: (() -> Unit)? = null,
	onAddBlock: (dateEpochDay: Long) -> Unit,
	onEditBlock: (blockId: Long, dateEpochDay: Long) -> Unit,
	onAddHabit: () -> Unit,
	dayViewModel: CalendarDayViewModel = hiltViewModel(),
	feedViewModel: CalendarFeedViewModel = hiltViewModel(),
	monthViewModel: CalendarMonthViewModel = hiltViewModel(),
	weekViewModel: CalendarWeekViewModel = hiltViewModel(),
	sessionViewModel: CalendarSessionViewModel = hiltViewModel(),
) {
	var view by rememberSaveable { mutableStateOf(CalendarView.Day) }
	var showCreateSheet by rememberSaveable { mutableStateOf(false) }
	var dayTimelineMode by rememberSaveable { mutableStateOf(false) }
	var restored by rememberSaveable { mutableStateOf(false) }
	val dayDate by dayViewModel.date.collectAsStateWithLifecycle()
	val weekDate by weekViewModel.selectedDate.collectAsStateWithLifecycle()
	val month by monthViewModel.month.collectAsStateWithLifecycle()
	val savedState by sessionViewModel.savedState.collectAsStateWithLifecycle()
	val snackbarHostState = remember { SnackbarHostState() }
	val undoLabel = stringResource(R.string.habit_mark_undo)
	val doneMsg = stringResource(R.string.habit_mark_snackbar_done)
	val skipMsg = stringResource(R.string.habit_mark_snackbar_skipped)

	LaunchedEffect(Unit) {
		dayViewModel.undoEvents.collect { event ->
			val message = if (event.newResult == HabitHistoryResult.DONE) doneMsg else skipMsg
			val result = snackbarHostState.showSnackbar(message = message, actionLabel = undoLabel)
			if (result == SnackbarResult.ActionPerformed) {
				dayViewModel.undo(event)
			}
		}
	}
	LaunchedEffect(Unit) {
		feedViewModel.undoEvents.collect { event ->
			val message = if (event.newResult == HabitHistoryResult.DONE) doneMsg else skipMsg
			val result = snackbarHostState.showSnackbar(message = message, actionLabel = undoLabel)
			if (result == SnackbarResult.ActionPerformed) {
				feedViewModel.undo(event)
			}
		}
	}

	LaunchedEffect(savedState) {
		if (restored) return@LaunchedEffect
		val saved = savedState ?: return@LaunchedEffect
		if (saved.isStored) {
			val restoredView = CalendarView.entries.find { it.name == saved.viewName }
			if (restoredView != null) {
				view = restoredView
			}
			val date = LocalDate.ofEpochDay(saved.dateEpochDay)
			dayViewModel.selectDate(date)
			weekViewModel.selectDate(date)
		}
		restored = true
	}

	LaunchedEffect(view, dayDate, weekDate, month, restored) {
		if (!restored) return@LaunchedEffect
		val date = when (view) {
			CalendarView.Day, CalendarView.Feed -> dayDate
			CalendarView.Week -> weekDate
			CalendarView.Month -> {
				val today = LocalDate.now()
				when {
					YearMonth.from(dayDate) == month -> dayDate
					YearMonth.from(today) == month -> today
					else -> month.atDay(1)
				}
			}
		}
		sessionViewModel.persist(view, date)
	}

	val createDate = when (view) {
		CalendarView.Day -> dayDate
		CalendarView.Week -> weekDate
		CalendarView.Month -> {
			val today = LocalDate.now()
			when {
				YearMonth.from(dayDate) == month -> dayDate
				YearMonth.from(today) == month -> today
				else -> month.atDay(1)
			}
		}
		CalendarView.Feed -> dayDate
	}
	val createDateEpochDay = createDate.toEpochDay()
	val showCreateFab = view == CalendarView.Day ||
		view == CalendarView.Week ||
		view == CalendarView.Month

	fun openDay(date: LocalDate) {
		dayViewModel.selectDate(date)
		weekViewModel.selectDate(date)
		view = CalendarView.Day
	}

	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text(stringResource(R.string.calendar_title)) },
				navigationIcon = {
					if (onBack != null) {
						IconButton(onClick = onBack) {
							Icon(
								Icons.AutoMirrored.Filled.ArrowBack,
								contentDescription = stringResource(R.string.action_back),
							)
						}
					}
				},
			)
		},
		snackbarHost = { SnackbarHost(snackbarHostState) },
		floatingActionButton = {
			if (showCreateFab) {
				FloatingActionButton(onClick = { showCreateSheet = true }) {
					Icon(Icons.Default.Add, contentDescription = stringResource(R.string.action_add))
				}
			}
		},
	) { padding ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(padding),
		) {
			CalendarViewSwitcher(
				selected = view,
				onSelected = { view = it },
			)
			when (view) {
				CalendarView.Day -> CalendarDayBody(
					viewModel = dayViewModel,
					timelineMode = dayTimelineMode,
					onTimelineModeChange = { dayTimelineMode = it },
					onEditBlock = onEditBlock,
					onAddBlock = { onAddBlock(dayDate.toEpochDay()) },
					onAddHabit = onAddHabit,
				)
				CalendarView.Feed -> CalendarFeedBody(
					viewModel = feedViewModel,
					onEditBlock = onEditBlock,
				)
				CalendarView.Month -> CalendarMonthBody(
					viewModel = monthViewModel,
					onOpenDay = ::openDay,
				)
				CalendarView.Week -> CalendarWeekBody(
					viewModel = weekViewModel,
					onOpenDay = ::openDay,
				)
			}
		}
	}

	if (showCreateSheet && showCreateFab) {
		CalendarCreateSheet(
			date = createDate,
			onDismiss = { showCreateSheet = false },
			onAddBlock = { onAddBlock(createDateEpochDay) },
			onAddHabit = onAddHabit,
		)
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CalendarViewSwitcher(
	selected: CalendarView,
	onSelected: (CalendarView) -> Unit,
) {
	SecondaryScrollableTabRow(
		selectedTabIndex = selected.ordinal,
		modifier = Modifier.fillMaxWidth(),
		edgePadding = dimensionResource(R.dimen.spacing_sm),
	) {
		CalendarView.entries.forEach { mode ->
			Tab(
				selected = mode == selected,
				onClick = { onSelected(mode) },
				text = { Text(stringResource(mode.titleRes)) },
			)
		}
	}
}

@Composable
private fun CalendarDayBody(
	viewModel: CalendarDayViewModel,
	timelineMode: Boolean,
	onTimelineModeChange: (Boolean) -> Unit,
	onEditBlock: (blockId: Long, dateEpochDay: Long) -> Unit,
	onAddBlock: () -> Unit,
	onAddHabit: () -> Unit,
) {
	val date by viewModel.date.collectAsStateWithLifecycle()
	val dayBusy by viewModel.dayBusy.collectAsStateWithLifecycle()
	val dueHabits by viewModel.dueHabits.collectAsStateWithLifecycle()
	val categoryColors by viewModel.categoryColors.collectAsStateWithLifecycle()

	Column(modifier = Modifier.fillMaxSize()) {
		CalendarDayNav(
			date = date,
			onPrevious = viewModel::goPreviousDay,
			onNext = viewModel::goNextDay,
			onToday = viewModel::goToday,
			onDateSelected = viewModel::selectDate,
		)
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = dimensionResource(R.dimen.screen_padding)),
			horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_sm)),
		) {
			FilterChip(
				selected = !timelineMode,
				onClick = { onTimelineModeChange(false) },
				label = { Text(stringResource(R.string.calendar_day_mode_list)) },
			)
			FilterChip(
				selected = timelineMode,
				onClick = { onTimelineModeChange(true) },
				label = { Text(stringResource(R.string.calendar_day_mode_timeline)) },
			)
		}
		CalendarDayContent(
			dayBusy = dayBusy,
			dueHabits = dueHabits,
			categoryColors = categoryColors,
			timelineMode = timelineMode,
			onMarkDone = viewModel::markDone,
			onMarkSkip = viewModel::markSkip,
			onIntervalClick = { interval ->
				if (interval.blockId > 0L && interval.source == TimeBlockSource.MANUAL) {
					onEditBlock(interval.blockId, date.toEpochDay())
				}
			},
			onAddBlock = onAddBlock,
			onAddHabit = onAddHabit,
			onSwipePrevious = viewModel::goPreviousDay,
			onSwipeNext = viewModel::goNextDay,
			modifier = Modifier.weight(1f),
		)
	}
}

@Composable
private fun CalendarFeedBody(
	viewModel: CalendarFeedViewModel,
	onEditBlock: (blockId: Long, dateEpochDay: Long) -> Unit,
) {
	val state by viewModel.state.collectAsStateWithLifecycle()
	CalendarFeedContent(
		state = state,
		onToggleSource = viewModel::toggleSource,
		onClearFilters = viewModel::clearFilters,
		onEditBlock = onEditBlock,
		onMarkDone = viewModel::markDone,
		onMarkSkip = viewModel::markSkip,
		rootModifier = Modifier.fillMaxSize(),
	)
}

@Composable
private fun CalendarMonthBody(
	viewModel: CalendarMonthViewModel,
	onOpenDay: (LocalDate) -> Unit,
) {
	val state by viewModel.state.collectAsStateWithLifecycle()
	CalendarMonthContent(
		state = state,
		onPrevious = viewModel::goPreviousMonth,
		onNext = viewModel::goNextMonth,
		onToday = viewModel::goCurrentMonth,
		onOpenDay = onOpenDay,
		rootModifier = Modifier.fillMaxSize(),
	)
}

@Composable
private fun CalendarWeekBody(
	viewModel: CalendarWeekViewModel,
	onOpenDay: (LocalDate) -> Unit,
) {
	val state by viewModel.state.collectAsStateWithLifecycle()
	CalendarWeekContent(
		state = state,
		onPrevious = viewModel::goPreviousWeek,
		onNext = viewModel::goNextWeek,
		onToday = viewModel::goToday,
		onSelectDay = viewModel::selectDate,
		onOpenDay = { date ->
			viewModel.selectDate(date)
			onOpenDay(date)
		},
		rootModifier = Modifier.fillMaxSize(),
	)
}
