package app.lade.calendar.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.lade.calendar.domain.CalendarWeekUiState
import app.lade.calendar.domain.WeekDayRow
import app.lade.resources.R
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarWeekContent(
	state: CalendarWeekUiState,
	onPrevious: () -> Unit,
	onNext: () -> Unit,
	onToday: () -> Unit,
	onSelectDay: (LocalDate) -> Unit,
	onOpenDay: (LocalDate) -> Unit,
	modifier: Modifier = Modifier,
) {
	val dateFmt = remember {
		DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.getDefault())
	}
	var gridMode by rememberSaveable { mutableStateOf(true) }

	Column(modifier = modifier.fillMaxSize()) {
		WeekNav(
			weekStart = state.weekStart,
			weekEnd = state.weekStart.plusDays(6),
			onPrevious = onPrevious,
			onNext = onNext,
			onToday = onToday,
		)
		WeekStrip(
			days = state.days,
			onSelectDay = onSelectDay,
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = dimensionResource(R.dimen.screen_padding)),
		)
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = dimensionResource(R.dimen.screen_padding)),
			horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_sm)),
		) {
			FilterChip(
				selected = !gridMode,
				onClick = { gridMode = false },
				label = { Text(stringResource(R.string.calendar_week_mode_list)) },
			)
			FilterChip(
				selected = gridMode,
				onClick = { gridMode = true },
				label = { Text(stringResource(R.string.calendar_week_mode_grid)) },
			)
		}
		if (gridMode) {
			CalendarWeekTimeGrid(
				days = state.days,
				onOpenDay = onOpenDay,
				modifier = Modifier
					.weight(1f)
					.fillMaxWidth(),
			)
		} else {
			LazyColumn(
				modifier = Modifier.fillMaxSize(),
				contentPadding = PaddingValues(bottom = dimensionResource(R.dimen.screen_padding)),
			) {
				items(state.days, key = { it.date.toEpochDay() }) { row ->
					val weekday = row.date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
					val freeLabel = formatDurationHoursMinutes(row.dayBusy.free)
					ListItem(
						headlineContent = {
							Text(
								text = stringResource(
									R.string.calendar_week_day_title,
									weekday,
									row.date.format(dateFmt),
								),
								color = when {
									row.isSelected -> MaterialTheme.colorScheme.primary
									row.isToday -> MaterialTheme.colorScheme.tertiary
									else -> MaterialTheme.colorScheme.onSurface
								},
							)
						},
						supportingContent = {
							Column {
								Text(
									stringResource(
										R.string.calendar_week_day_summary,
										row.dayBusy.intervals.size,
										freeLabel,
										row.dueHabitsCount,
									),
								)
								if (row.previewTitles.isNotEmpty()) {
									Text(
										text = previewLine(row),
										style = MaterialTheme.typography.bodySmall,
										color = MaterialTheme.colorScheme.onSurfaceVariant,
										maxLines = 2,
										overflow = TextOverflow.Ellipsis,
									)
								}
							}
						},
						modifier = Modifier
							.fillMaxWidth()
							.clickable { onOpenDay(row.date) },
					)
				}
			}
		}
	}
}

@Composable
private fun WeekStrip(
	days: List<WeekDayRow>,
	onSelectDay: (LocalDate) -> Unit,
	modifier: Modifier = Modifier,
) {
	Row(
		modifier = modifier.padding(vertical = dimensionResource(R.dimen.spacing_sm)),
		horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_xs)),
	) {
		days.forEach { day ->
			WeekStripCell(
				day = day,
				onClick = { onSelectDay(day.date) },
				modifier = Modifier.weight(1f),
			)
		}
	}
}

@Composable
private fun WeekStripCell(
	day: WeekDayRow,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	val shape = RoundedCornerShape(dimensionResource(R.dimen.spacing_sm))
	val weekday = day.date.dayOfWeek.getDisplayName(TextStyle.NARROW, Locale.getDefault())
	val borderColor = when {
		day.isSelected -> MaterialTheme.colorScheme.primary
		day.isToday -> MaterialTheme.colorScheme.tertiary
		else -> MaterialTheme.colorScheme.outlineVariant
	}
	val container = when {
		day.isSelected -> MaterialTheme.colorScheme.primaryContainer
		else -> MaterialTheme.colorScheme.surface
	}
	Column(
		modifier = modifier
			.clip(shape)
			.border(
				width = 1.dp,
				color = borderColor,
				shape = shape,
			)
			.background(container)
			.clickable(onClick = onClick)
			.padding(vertical = dimensionResource(R.dimen.spacing_sm)),
		horizontalAlignment = Alignment.CenterHorizontally,
	) {
		Text(
			text = weekday,
			style = MaterialTheme.typography.labelSmall,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
		)
		Text(
			text = day.date.dayOfMonth.toString(),
			style = MaterialTheme.typography.titleSmall,
			color = if (day.isSelected) {
				MaterialTheme.colorScheme.onPrimaryContainer
			} else {
				MaterialTheme.colorScheme.onSurface
			},
		)
		Text(
			text = stringResource(
				R.string.calendar_week_strip_busy,
				day.dayBusy.intervals.size,
			),
			style = MaterialTheme.typography.labelSmall,
			color = MaterialTheme.colorScheme.primary,
			textAlign = TextAlign.Center,
		)
		Text(
			text = stringResource(
				R.string.calendar_week_strip_habits,
				day.dueHabitsCount,
			),
			style = MaterialTheme.typography.labelSmall,
			color = MaterialTheme.colorScheme.tertiary,
			textAlign = TextAlign.Center,
		)
	}
}

@Composable
private fun previewLine(row: WeekDayRow): String {
	val base = row.previewTitles.joinToString(" · ")
	return if (row.previewOverflow > 0) {
		"$base · ${stringResource(R.string.calendar_week_preview_more, row.previewOverflow)}"
	} else {
		base
	}
}

@Composable
private fun WeekNav(
	weekStart: LocalDate,
	weekEnd: LocalDate,
	onPrevious: () -> Unit,
	onNext: () -> Unit,
	onToday: () -> Unit,
) {
	val dateFmt = remember {
		DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.getDefault())
	}
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = dimensionResource(R.dimen.spacing_sm)),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.SpaceBetween,
	) {
		IconButton(onClick = onPrevious) {
			Icon(
				Icons.AutoMirrored.Filled.KeyboardArrowLeft,
				contentDescription = stringResource(R.string.calendar_prev_week),
			)
		}
		Row(verticalAlignment = Alignment.CenterVertically) {
			Text(
				text = stringResource(
					R.string.calendar_week_range,
					weekStart.format(dateFmt),
					weekEnd.format(dateFmt),
				),
				style = MaterialTheme.typography.titleSmall,
			)
			TextButton(onClick = onToday) {
				Text(stringResource(R.string.calendar_today))
			}
		}
		IconButton(onClick = onNext) {
			Icon(
				Icons.AutoMirrored.Filled.KeyboardArrowRight,
				contentDescription = stringResource(R.string.calendar_next_week),
			)
		}
	}
}

@Composable
private fun formatDurationHoursMinutes(duration: Duration): String {
	val totalMinutes = duration.toMinutes().coerceAtLeast(0)
	val hours = totalMinutes / 60
	val minutes = totalMinutes % 60
	return stringResource(R.string.format_duration_hm, hours, minutes)
}