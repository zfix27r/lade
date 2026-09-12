package app.lade.calendar.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarViewDay
import androidx.compose.material.icons.filled.CalendarViewWeek
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.lade.resources.R
import app.lade.entry.data.EntryHistoryResult
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.util.Locale

private const val SCALE_ZOOM_IN = 1.2f
private const val SCALE_ZOOM_OUT = 0.85f

enum class CalendarView(
	@param:StringRes val titleRes: Int,
	val icon: ImageVector,
) {
	Day(R.string.calendar_view_day, Icons.Filled.CalendarViewDay),
	Week(R.string.calendar_view_week, Icons.Filled.CalendarViewWeek),
	Month(R.string.calendar_view_month, Icons.Filled.CalendarMonth),
	Year(R.string.calendar_view_year, Icons.Filled.DateRange),
	Feed(R.string.calendar_view_feed, Icons.AutoMirrored.Filled.ViewList),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
	onBack: (() -> Unit)? = null,
	onCreateEntry: (kind: String, dateEpochDay: Long) -> Unit,
	onEditEntry: (entryId: Long) -> Unit,
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
	var pickingDate by rememberSaveable { mutableStateOf(false) }
	var viewMenuExpanded by rememberSaveable { mutableStateOf(false) }
	val dayDate by dayViewModel.date.collectAsStateWithLifecycle()
	val weekDate by weekViewModel.selectedDate.collectAsStateWithLifecycle()
	val weekState by weekViewModel.state.collectAsStateWithLifecycle()
	val month by monthViewModel.month.collectAsStateWithLifecycle()
	val savedState by sessionViewModel.savedState.collectAsStateWithLifecycle()
	val snackbarHostState = remember { SnackbarHostState() }
	val undoLabel = stringResource(R.string.habit_mark_undo)
	val doneMsg = stringResource(R.string.habit_mark_snackbar_done)
	val skipMsg = stringResource(R.string.habit_mark_snackbar_skipped)
	val locale = remember { Locale.getDefault() }
	val dayTitleFmt = remember(locale) {
		DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale)
	}
	val weekTitleFmt = remember(locale) {
		DateTimeFormatter.ofPattern("d MMM", locale)
	}

	LaunchedEffect(Unit) {
		dayViewModel.undoEvents.collect { event ->
			val message = if (event.newResult == EntryHistoryResult.DONE) doneMsg else skipMsg
			val result = snackbarHostState.showSnackbar(message = message, actionLabel = undoLabel)
			if (result == SnackbarResult.ActionPerformed) {
				dayViewModel.undo(event)
			}
		}
	}
	LaunchedEffect(Unit) {
		feedViewModel.undoEvents.collect { event ->
			val message = if (event.newResult == EntryHistoryResult.DONE) doneMsg else skipMsg
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
			monthViewModel.selectMonth(YearMonth.from(date))
		}
		restored = true
	}

	LaunchedEffect(view, dayDate, weekDate, month, restored) {
		if (!restored) return@LaunchedEffect
		val date = when (view) {
			CalendarView.Day, CalendarView.Feed -> dayDate
			CalendarView.Week -> weekDate
			CalendarView.Month, CalendarView.Year -> {
				val today = LocalDate.now()
				when {
					YearMonth.from(dayDate) == month -> dayDate
					view == CalendarView.Year && today.year == month.year -> today
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
		CalendarView.Month, CalendarView.Year -> {
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
		monthViewModel.selectMonth(YearMonth.from(date))
		view = CalendarView.Day
	}

	fun openMonth(yearMonth: YearMonth) {
		monthViewModel.selectMonth(yearMonth)
		view = CalendarView.Month
	}

	fun applyPickedDate(date: LocalDate) {
		dayViewModel.selectDate(date)
		weekViewModel.selectDate(date)
		monthViewModel.selectMonth(YearMonth.from(date))
		if (view == CalendarView.Feed || view == CalendarView.Month || view == CalendarView.Year) {
			view = CalendarView.Day
		}
	}

	val pickerDate = when (view) {
		CalendarView.Day, CalendarView.Feed -> dayDate
		CalendarView.Week -> weekDate
		CalendarView.Month, CalendarView.Year -> {
			val today = LocalDate.now()
			when {
				YearMonth.from(dayDate) == month -> dayDate
				view == CalendarView.Year && today.year == month.year -> today
				YearMonth.from(today) == month -> today
				else -> month.atDay(1)
			}
		}
	}

	val appBarTitle = when (view) {
		CalendarView.Day, CalendarView.Feed -> dayDate.format(dayTitleFmt)
		CalendarView.Week -> {
			val start = weekState.weekStart
			val end = start.plusDays(6)
			stringResource(
				R.string.calendar_week_range,
				start.format(weekTitleFmt),
				end.format(weekTitleFmt),
			)
		}
		CalendarView.Month -> {
			val name = month.month.getDisplayName(TextStyle.FULL_STANDALONE, locale)
			stringResource(R.string.calendar_month_title, name, month.year)
		}
		CalendarView.Year -> stringResource(R.string.calendar_year_title, month.year)
	}

	Scaffold(
		topBar = {
			TopAppBar(
				title = {
					TextButton(onClick = { pickingDate = true }) {
						Text(
							text = appBarTitle,
							style = MaterialTheme.typography.titleLarge,
							color = MaterialTheme.colorScheme.onSurface,
						)
					}
				},
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
				actions = {
					Box {
						IconButton(onClick = { viewMenuExpanded = true }) {
							Icon(
								imageVector = view.icon,
								contentDescription = stringResource(R.string.calendar_view_menu_cd),
							)
						}
						DropdownMenu(
							expanded = viewMenuExpanded,
							onDismissRequest = { viewMenuExpanded = false },
						) {
							CalendarView.entries.forEach { mode ->
								DropdownMenuItem(
									text = { Text(stringResource(mode.titleRes)) },
									onClick = {
										view = mode
										viewMenuExpanded = false
									},
									leadingIcon = {
										Icon(mode.icon, contentDescription = null)
									},
									trailingIcon = if (mode == view) {
										{
											Icon(
												Icons.Filled.Check,
												contentDescription = null,
												tint = MaterialTheme.colorScheme.primary,
											)
										}
									} else {
										null
									},
								)
							}
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
				.padding(padding)
				.pointerInput(view) {
					if (view == CalendarView.Feed) return@pointerInput
					var accum = 1f
					detectTransformGestures { _, _, zoom, _ ->
						accum *= zoom
						when {
							accum >= SCALE_ZOOM_IN -> {
								val next = when (view) {
									CalendarView.Year -> CalendarView.Month
									CalendarView.Month -> CalendarView.Week
									CalendarView.Week -> CalendarView.Day
									else -> null
								}
								if (next != null) view = next
								accum = 1f
							}
							accum <= SCALE_ZOOM_OUT -> {
								val next = when (view) {
									CalendarView.Day -> CalendarView.Week
									CalendarView.Week -> CalendarView.Month
									CalendarView.Month -> CalendarView.Year
									else -> null
								}
								if (next != null) view = next
								accum = 1f
							}
						}
					}
				},
		) {
			when (view) {
				CalendarView.Day -> CalendarDayBody(
					viewModel = dayViewModel,
					timelineMode = dayTimelineMode,
					onTimelineModeChange = { dayTimelineMode = it },
					onEditEntry = onEditEntry,
					onAddEntry = { showCreateSheet = true },
				)
				CalendarView.Feed -> CalendarFeedBody(
					viewModel = feedViewModel,
					onEditEntry = onEditEntry,
				)
				CalendarView.Month -> CalendarMonthBody(
					viewModel = monthViewModel,
					onOpenDay = ::openDay,
				)
				CalendarView.Week -> CalendarWeekBody(
					viewModel = weekViewModel,
					onOpenDay = ::openDay,
				)
				CalendarView.Year -> CalendarYearBody(
					year = month.year,
					onPrevious = { monthViewModel.selectMonth(month.minusYears(1)) },
					onNext = { monthViewModel.selectMonth(month.plusYears(1)) },
					onToday = monthViewModel::goCurrentMonth,
					onOpenMonth = ::openMonth,
				)
			}
		}
	}

	if (pickingDate) {
		val state = rememberDatePickerState(
			initialSelectedDateMillis = pickerDate
				.atStartOfDay(ZoneOffset.UTC)
				.toInstant()
				.toEpochMilli(),
		)
		DatePickerDialog(
			onDismissRequest = { pickingDate = false },
			confirmButton = {
				TextButton(
					onClick = {
						state.selectedDateMillis?.let { millis ->
							applyPickedDate(
								Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate(),
							)
						}
						pickingDate = false
					},
				) {
					Text(stringResource(R.string.action_ok))
				}
			},
			dismissButton = {
				Row {
					TextButton(
						onClick = {
							dayViewModel.goToday()
							weekViewModel.goToday()
							monthViewModel.goCurrentMonth()
							pickingDate = false
						},
					) {
						Text(stringResource(R.string.calendar_today))
					}
					TextButton(onClick = { pickingDate = false }) {
						Text(stringResource(R.string.action_cancel))
					}
				}
			},
		) {
			DatePicker(state = state)
		}
	}

	if (showCreateSheet && showCreateFab) {
		CalendarCreateSheet(
			date = createDate,
			onDismiss = { showCreateSheet = false },
			onCreate = { kind ->
				onCreateEntry(kind.storage, createDateEpochDay)
			},
		)
	}
}

@Composable
private fun CalendarDayBody(
	viewModel: CalendarDayViewModel,
	timelineMode: Boolean,
	onTimelineModeChange: (Boolean) -> Unit,
	onEditEntry: (entryId: Long) -> Unit,
	onAddEntry: () -> Unit,
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
				if (interval.entryId > 0L) onEditEntry(interval.entryId)
			},
			onAddBlock = onAddEntry,
			onAddHabit = onAddEntry,
			onSwipePrevious = viewModel::goPreviousDay,
			onSwipeNext = viewModel::goNextDay,
			modifier = Modifier.weight(1f),
		)
	}
}

@Composable
private fun CalendarFeedBody(
	viewModel: CalendarFeedViewModel,
	onEditEntry: (entryId: Long) -> Unit,
) {
	val state by viewModel.state.collectAsStateWithLifecycle()
	CalendarFeedContent(
		state = state,
		onToggleSource = viewModel::toggleSource,
		onClearFilters = viewModel::clearFilters,
		onEditEntry = onEditEntry,
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
private fun CalendarYearBody(
	year: Int,
	onPrevious: () -> Unit,
	onNext: () -> Unit,
	onToday: () -> Unit,
	onOpenMonth: (YearMonth) -> Unit,
) {
	CalendarYearContent(
		year = year,
		onPrevious = onPrevious,
		onNext = onNext,
		onToday = onToday,
		onOpenMonth = onOpenMonth,
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
