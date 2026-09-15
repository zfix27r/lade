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
import app.lade.calendar.domain.FeedFilterOption
import app.lade.calendar.domain.FeedItem
import app.lade.calendar.domain.FeedListEntry
import app.lade.calendar.domain.FeedSource
import app.lade.resources.R
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@Composable
fun CalendarFeedContent(
	state: CalendarFeedUiState,
	onToggleSource: (FeedSource) -> Unit,
	onClearFilters: () -> Unit,
	onEditEntry: (entryId: Long) -> Unit,
	onMarkDone: (habitId: Long, dateEpochDay: Long) -> Unit,
	onMarkSkip: (habitId: Long, dateEpochDay: Long) -> Unit,
	modifier: Modifier = Modifier,
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
	)
	val listState = rememberLazyListState()
	val scope = rememberCoroutineScope()
	val todayHeaderIndex = remember(state.entries, state.todayEpochDay) {
		state.entries.indexOfFirst {
			it is FeedListEntry.DayHeader && it.dateEpochDay == state.todayEpochDay
		}
	}

	Column(modifier = modifier.fillMaxSize()) {
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
								onEditEntry = onEditEntry,
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
	onEditEntry: (entryId: Long) -> Unit,
	onMarkDone: (habitId: Long, dateEpochDay: Long) -> Unit,
	onMarkSkip: (habitId: Long, dateEpochDay: Long) -> Unit,
) {
	val entryId = item.entryId
	val editable = entryId != null && entryId > 0L &&
			(item.source == FeedSource.MANUAL || item.source == FeedSource.SCHEDULE ||
					item.source == FeedSource.HABIT)
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
				EntryDayMarkActions(
					done = item.habitDone,
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
				.clickable { onEditEntry(entryId) }
		} else {
			Modifier.fillMaxWidth()
		},
	)
}