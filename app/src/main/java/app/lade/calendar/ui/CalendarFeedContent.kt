package app.lade.calendar.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import app.lade.R
import app.lade.habits.ui.habitUnitLabel
import app.lade.habits.ui.mark.HabitDayMarkActions
import app.lade.habits.ui.mark.habitMarkStatusLabel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

private data class FeedFilterOption(
	val source: String?,
	val labelRes: Int,
)

@Composable
fun CalendarFeedContent(
	state: CalendarFeedUiState,
	onToggleSource: (String) -> Unit,
	onClearFilters: () -> Unit,
	onEditBlock: (blockId: Long, dateEpochDay: Long) -> Unit,
	onMarkDone: (habitId: Long, dateEpochDay: Long) -> Unit,
	onMarkSkip: (habitId: Long, dateEpochDay: Long) -> Unit,
	rootModifier: Modifier = Modifier,
) {
	val timePattern = stringResource(R.string.format_time_hm)
	val timeFmt = remember(timePattern) { DateTimeFormatter.ofPattern(timePattern) }
	val dateFmt = remember {
		DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.getDefault())
	}
	val filters = listOf(
		FeedFilterOption(null, R.string.calendar_feed_filter_all),
		FeedFilterOption(FeedSource.SCHEDULE, R.string.calendar_feed_filter_schedule),
		FeedFilterOption(FeedSource.MANUAL, R.string.calendar_feed_filter_manual),
		FeedFilterOption(FeedSource.HABIT, R.string.calendar_feed_filter_habit),
		FeedFilterOption(FeedSource.HEALTH, R.string.calendar_feed_filter_health),
		FeedFilterOption(FeedSource.CALENDAR, R.string.calendar_feed_filter_calendar),
	)
	val listState = rememberLazyListState()
	val scope = rememberCoroutineScope()
	val todayHeaderIndex = remember(state.entries, state.todayEpochDay) {
		state.entries.indexOfFirst {
			it is FeedListEntry.DayHeader && it.dateEpochDay == state.todayEpochDay
		}
	}

	Column(modifier = rootModifier.fillMaxSize()) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = dimensionResource(R.dimen.screen_padding)),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceBetween,
		) {
			TextButton(
				onClick = {
					if (todayHeaderIndex >= 0) {
						scope.launch { listState.animateScrollToItem(todayHeaderIndex) }
					}
				},
				enabled = todayHeaderIndex >= 0,
			) {
				Text(stringResource(R.string.calendar_feed_jump_today))
			}
		}
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.horizontalScroll(rememberScrollState())
				.padding(
					horizontal = dimensionResource(R.dimen.screen_padding),
					vertical = dimensionResource(R.dimen.spacing_sm),
				),
			horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_sm)),
		) {
			filters.forEach { option ->
				val selected = if (option.source == null) {
					state.selectedSources.isEmpty()
				} else {
					option.source in state.selectedSources
				}
				FilterChip(
					selected = selected,
					onClick = {
						if (option.source == null) {
							onClearFilters()
						} else {
							onToggleSource(option.source)
						}
					},
					label = { Text(stringResource(option.labelRes)) },
				)
			}
		}
		if (state.entries.isEmpty()) {
			Text(
				text = stringResource(R.string.calendar_feed_empty),
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
				modifier = Modifier
					.fillMaxWidth()
					.padding(dimensionResource(R.dimen.screen_padding)),
			)
		} else {
			LazyColumn(
				state = listState,
				modifier = Modifier.fillMaxSize(),
				contentPadding = PaddingValues(bottom = dimensionResource(R.dimen.screen_padding)),
			) {
				itemsIndexed(
					items = state.entries,
					key = { _, entry ->
						when (entry) {
							is FeedListEntry.DayHeader -> "day-${entry.dateEpochDay}"
							is FeedListEntry.Row -> entry.item.key
						}
					},
				) { _, entry ->
					when (entry) {
						is FeedListEntry.DayHeader -> {
							val label = LocalDate.ofEpochDay(entry.dateEpochDay).format(dateFmt)
							val isToday = entry.dateEpochDay == state.todayEpochDay
							Text(
								text = if (isToday) {
									"${stringResource(R.string.calendar_today)} · $label"
								} else {
									label
								},
								style = MaterialTheme.typography.titleSmall,
								color = if (isToday) {
									MaterialTheme.colorScheme.primary
								} else {
									MaterialTheme.colorScheme.onSurface
								},
								modifier = Modifier
									.fillMaxWidth()
									.padding(
										horizontal = dimensionResource(R.dimen.screen_padding),
										vertical = dimensionResource(R.dimen.spacing_sm),
									),
							)
						}
						is FeedListEntry.Row -> {
							FeedRow(
								item = entry.item,
								timeFmt = timeFmt,
								onEditBlock = onEditBlock,
								onMarkDone = onMarkDone,
								onMarkSkip = onMarkSkip,
							)
						}
					}
				}
			}
		}
	}
}

@Composable
private fun FeedRow(
	item: FeedItem,
	timeFmt: DateTimeFormatter,
	onEditBlock: (blockId: Long, dateEpochDay: Long) -> Unit,
	onMarkDone: (habitId: Long, dateEpochDay: Long) -> Unit,
	onMarkSkip: (habitId: Long, dateEpochDay: Long) -> Unit,
) {
	val editable = item.blockId != null && item.source == FeedSource.MANUAL
	val habitId = item.habitId
	ListItem(
		headlineContent = {
			Text(
				item.title.ifBlank {
					stringResource(R.string.time_block_untitled)
				},
			)
		},
		supportingContent = {
			Column {
				feedDetail(item, timeFmt)?.let { Text(it) }
				Text(
					text = feedSourceLabel(item.source),
					style = MaterialTheme.typography.labelMedium,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
				)
			}
		},
		trailingContent = if (habitId != null) {
			{
				HabitDayMarkActions(
					result = item.habitResult,
					onDone = { onMarkDone(habitId, item.dateEpochDay) },
					onSkip = { onMarkSkip(habitId, item.dateEpochDay) },
				)
			}
		} else {
			null
		},
		modifier = if (editable) {
			Modifier
				.fillMaxWidth()
				.clickable { onEditBlock(item.blockId!!, item.dateEpochDay) }
		} else {
			Modifier.fillMaxWidth()
		},
	)
}

@Composable
private fun feedDetail(item: FeedItem, timeFmt: DateTimeFormatter): String? {
	val start = item.startMinutes
	val end = item.endMinutes
	if (start != null && end != null) {
		return stringResource(
			R.string.format_time_range,
			LocalTime.ofSecondOfDay(start * 60L).format(timeFmt),
			LocalTime.ofSecondOfDay(end * 60L).format(timeFmt),
		)
	}
	if (item.source == FeedSource.HABIT) {
		val status = habitMarkStatusLabel(item.habitResult)
		val unit = item.habitGoalUnit?.let { habitUnitLabel(it) }
		val goal = if (item.habitGoalValue != null && unit != null) {
			stringResource(R.string.format_habit_goal, item.habitGoalValue, unit)
		} else {
			null
		}
		val time = item.habitTimeMinutes?.let { minutes ->
			LocalTime.ofSecondOfDay(minutes * 60L).format(timeFmt)
		}
		return listOfNotNull(status, goal, time).joinToString(" · ").ifBlank { null }
	}
	return null
}

@Composable
private fun feedSourceLabel(source: String): String = stringResource(
	when (source) {
		FeedSource.SCHEDULE -> R.string.calendar_feed_filter_schedule
		FeedSource.MANUAL -> R.string.calendar_feed_filter_manual
		FeedSource.HABIT -> R.string.calendar_feed_filter_habit
		FeedSource.HEALTH -> R.string.calendar_feed_filter_health
		FeedSource.CALENDAR -> R.string.calendar_feed_filter_calendar
		else -> R.string.calendar_feed_filter_manual
	},
)
