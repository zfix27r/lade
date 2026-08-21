package app.lade.calendar.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import app.lade.R
import app.lade.categories.ui.CategoryColorIndicator
import app.lade.habits.domain.model.Habit
import app.lade.habits.ui.habitUnitLabel
import app.lade.habits.ui.mark.HabitDayMarkActions
import app.lade.habits.ui.mark.habitMarkStatusLabel
import app.lade.time.domain.BusyInterval
import app.lade.time.domain.DayBusy
import app.lade.time.domain.model.TimeBlockSource
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun CalendarDayContent(
	dayBusy: DayBusy,
	dueHabits: List<DueHabitItem>,
	categoryColors: Map<Long, String>,
	timelineMode: Boolean,
	onMarkDone: (Habit) -> Unit,
	onMarkSkip: (Habit) -> Unit,
	onIntervalClick: (BusyInterval) -> Unit = {},
	onAddBlock: () -> Unit = {},
	onAddHabit: () -> Unit = {},
	onSwipePrevious: () -> Unit = {},
	onSwipeNext: () -> Unit = {},
	modifier: Modifier = Modifier,
) {
	val timePattern = stringResource(R.string.format_time_hm)
	val timeFmt = remember(timePattern) { DateTimeFormatter.ofPattern(timePattern) }
	val freeLabel = formatDurationHoursMinutes(dayBusy.free)
	val swipeThresholdPx = with(LocalDensity.current) {
		dimensionResource(R.dimen.calendar_swipe_threshold).toPx()
	}
	var dragTotal by remember { mutableFloatStateOf(0f) }

	val swipeModifier = Modifier.pointerInput(swipeThresholdPx) {
		detectHorizontalDragGestures(
			onDragEnd = {
				when {
					dragTotal > swipeThresholdPx -> onSwipePrevious()
					dragTotal < -swipeThresholdPx -> onSwipeNext()
				}
				dragTotal = 0f
			},
			onDragCancel = { dragTotal = 0f },
			onHorizontalDrag = { _, amount ->
				dragTotal += amount
			},
		)
	}

	if (timelineMode) {
		Column(modifier = modifier.fillMaxSize().then(swipeModifier)) {
			Text(
				text = stringResource(R.string.calendar_free_summary, freeLabel),
				style = MaterialTheme.typography.titleSmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
				modifier = Modifier
					.fillMaxWidth()
					.padding(
						horizontal = dimensionResource(R.dimen.screen_padding),
						vertical = dimensionResource(R.dimen.spacing_sm),
					),
			)
			SectionTitle(stringResource(R.string.calendar_section_habits))
			if (dueHabits.isEmpty()) {
				EmptySection(
					hint = stringResource(R.string.calendar_habits_empty),
					cta = stringResource(R.string.calendar_habit_add),
					onCta = onAddHabit,
				)
			} else {
				dueHabits.forEach { item ->
					DueHabitRow(
						item = item,
						categoryColorKey = categoryColors[item.habit.categoryId].orEmpty(),
						timeFmt = timeFmt,
						onMarkDone = { onMarkDone(item.habit) },
						onMarkSkip = { onMarkSkip(item.habit) },
					)
				}
			}
			SectionTitle(stringResource(R.string.calendar_section_busy))
			CalendarDayTimeline(
				intervals = dayBusy.intervals,
				onIntervalClick = onIntervalClick,
				modifier = Modifier.weight(1f),
			)
		}
		return
	}

	LazyColumn(
		modifier = modifier
			.fillMaxSize()
			.then(swipeModifier),
		contentPadding = PaddingValues(
			bottom = dimensionResource(R.dimen.screen_padding),
		),
		verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_xs)),
	) {
		item(key = "free") {
			Text(
				text = stringResource(R.string.calendar_free_summary, freeLabel),
				style = MaterialTheme.typography.titleSmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
				modifier = Modifier
					.fillMaxWidth()
					.padding(
						horizontal = dimensionResource(R.dimen.screen_padding),
						vertical = dimensionResource(R.dimen.spacing_sm),
					),
			)
		}
		// due first — actionable agenda
		item(key = "habits_header") {
			SectionTitle(stringResource(R.string.calendar_section_habits))
		}
		if (dueHabits.isEmpty()) {
			item(key = "habits_empty") {
				EmptySection(
					hint = stringResource(R.string.calendar_habits_empty),
					cta = stringResource(R.string.calendar_habit_add),
					onCta = onAddHabit,
				)
			}
		} else {
			items(dueHabits, key = { it.habit.id }) { item ->
				DueHabitRow(
					item = item,
					categoryColorKey = categoryColors[item.habit.categoryId].orEmpty(),
					timeFmt = timeFmt,
					onMarkDone = { onMarkDone(item.habit) },
					onMarkSkip = { onMarkSkip(item.habit) },
				)
			}
		}
		item(key = "busy_header") {
			SectionTitle(stringResource(R.string.calendar_section_busy))
		}
		if (dayBusy.intervals.isEmpty()) {
			item(key = "busy_empty") {
				EmptySection(
					hint = stringResource(R.string.calendar_busy_empty),
					cta = stringResource(R.string.calendar_busy_add),
					onCta = onAddBlock,
				)
			}
		} else {
			items(dayBusy.intervals, key = { it.listKey() }) { interval ->
				BusyIntervalRow(
					interval = interval,
					categoryColorKey = categoryColors[interval.categoryId].orEmpty(),
					timeFmt = timeFmt,
					onClick = {
						if (interval.blockId > 0L && interval.source == TimeBlockSource.MANUAL) {
							onIntervalClick(interval)
						}
					},
				)
			}
		}
	}
}

@Composable
private fun DueHabitRow(
	item: DueHabitItem,
	categoryColorKey: String,
	timeFmt: DateTimeFormatter,
	onMarkDone: () -> Unit,
	onMarkSkip: () -> Unit,
) {
	val status = habitMarkStatusLabel(item.result)
	ListItem(
		leadingContent = {
			CategoryColorIndicator(colorKey = categoryColorKey)
		},
		headlineContent = {
			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_sm)),
			) {
				Text(
					text = item.habit.title,
					style = MaterialTheme.typography.titleMedium,
					modifier = Modifier.weight(1f, fill = false),
				)
				if (status != null) {
					Text(
						text = status,
						style = MaterialTheme.typography.labelMedium,
						color = MaterialTheme.colorScheme.primary,
					)
				}
			}
		},
		supportingContent = {
			Text(
				text = habitSupporting(item.habit, timeFmt),
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
			)
		},
		trailingContent = {
			HabitDayMarkActions(
				result = item.result,
				onDone = onMarkDone,
				onSkip = onMarkSkip,
			)
		},
	)
}

@Composable
private fun BusyIntervalRow(
	interval: BusyInterval,
	categoryColorKey: String,
	timeFmt: DateTimeFormatter,
	onClick: () -> Unit,
) {
	val editable = interval.blockId > 0L && interval.source == TimeBlockSource.MANUAL
	ListItem(
		leadingContent = {
			CategoryColorIndicator(colorKey = categoryColorKey)
		},
		headlineContent = {
			Text(
				interval.title.ifBlank {
					stringResource(R.string.time_block_untitled)
				},
			)
		},
		supportingContent = {
			Text(
				stringResource(
					R.string.format_time_range,
					interval.start.format(timeFmt),
					interval.end.format(timeFmt),
				),
			)
		},
		trailingContent = {
			SourceChip(source = interval.source)
		},
		modifier = if (editable) {
			Modifier
				.fillMaxWidth()
				.clickable(onClick = onClick)
		} else {
			Modifier.fillMaxWidth()
		},
	)
}

@Composable
private fun SourceChip(source: String) {
	val (labelRes, icon) = sourceVisual(source)
	Row(
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_xs)),
	) {
		Icon(
			imageVector = icon,
			contentDescription = null,
			tint = MaterialTheme.colorScheme.onSurfaceVariant,
		)
		Text(
			text = stringResource(labelRes),
			style = MaterialTheme.typography.labelMedium,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
		)
	}
}

@Composable
private fun sourceVisual(source: String): Pair<Int, ImageVector> = when (source) {
	TimeBlockSource.MANUAL -> R.string.calendar_source_manual to Icons.Default.EditCalendar
	TimeBlockSource.HEALTH -> R.string.calendar_source_health to Icons.Default.Favorite
	TimeBlockSource.CALENDAR -> R.string.calendar_source_calendar to Icons.Default.Event
	else -> R.string.calendar_source_schedule to Icons.Default.Schedule
}

@Composable
private fun SectionTitle(text: String) {
	Text(
		text = text,
		style = MaterialTheme.typography.titleSmall,
		modifier = Modifier
			.fillMaxWidth()
			.padding(
				horizontal = dimensionResource(R.dimen.screen_padding),
				vertical = dimensionResource(R.dimen.spacing_sm),
			),
	)
}

@Composable
private fun EmptySection(
	hint: String,
	cta: String,
	onCta: () -> Unit,
) {
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = dimensionResource(R.dimen.screen_padding)),
	) {
		Text(
			text = hint,
			style = MaterialTheme.typography.bodyMedium,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
		)
		TextButton(onClick = onCta) {
			Text(cta)
		}
	}
}

@Composable
private fun habitSupporting(habit: Habit, timeFmt: DateTimeFormatter): String {
	val unit = habitUnitLabel(habit.goalUnit)
	val timeOfDay = habit.timeOfDayMinutes?.let { minutes ->
		LocalTime.ofSecondOfDay(minutes * 60L).format(timeFmt)
	}
	return if (timeOfDay != null) {
		stringResource(R.string.format_habit_goal_time, habit.goalValue, unit, timeOfDay)
	} else {
		stringResource(R.string.format_habit_goal, habit.goalValue, unit)
	}
}

@Composable
private fun formatDurationHoursMinutes(duration: Duration): String {
	val totalMinutes = duration.toMinutes().coerceAtLeast(0)
	val hours = totalMinutes / 60
	val minutes = totalMinutes % 60
	return stringResource(R.string.format_duration_hm, hours, minutes)
}

private fun BusyInterval.listKey(): String =
	if (blockId > 0L) "block-$blockId" else "schedule-$scheduleId-$start-$end"
