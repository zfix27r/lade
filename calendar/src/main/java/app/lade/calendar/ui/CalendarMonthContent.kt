package app.lade.calendar.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import app.lade.R
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarMonthContent(
	state: CalendarMonthUiState,
	onPrevious: () -> Unit,
	onNext: () -> Unit,
	onToday: () -> Unit,
	onOpenDay: (LocalDate) -> Unit,
	rootModifier: Modifier = Modifier,
) {
	Column(
		modifier = rootModifier
			.fillMaxSize()
			.padding(horizontal = dimensionResource(R.dimen.screen_padding)),
	) {
		MonthNav(
			month = state.month,
			onPrevious = onPrevious,
			onNext = onNext,
			onToday = onToday,
		)
		Row(modifier = Modifier.fillMaxWidth()) {
			state.weekdayLabels.forEach { day ->
				Text(
					text = weekdayShort(day),
					style = MaterialTheme.typography.labelMedium,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
					textAlign = TextAlign.Center,
					modifier = Modifier.weight(1f),
				)
			}
		}
		Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_sm)))
		state.days.chunked(7).forEach { week ->
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_xs)),
			) {
				week.forEach { cell ->
					MonthDayCellView(
						cell = cell,
						onOpenDay = onOpenDay,
						modifier = Modifier.weight(1f),
					)
				}
			}
		}
		Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_md)))
		MonthLegend()
	}
}

@Composable
private fun MonthLegend() {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_lg)),
		verticalAlignment = Alignment.CenterVertically,
	) {
		LegendItem(
			color = MaterialTheme.colorScheme.primary,
			label = stringResource(R.string.calendar_legend_busy),
		)
		LegendItem(
			color = MaterialTheme.colorScheme.tertiary,
			label = stringResource(R.string.calendar_legend_habits),
		)
	}
}

@Composable
private fun LegendItem(
	color: Color,
	label: String,
) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_sm)),
	) {
		MarkerDot(color = color)
		Text(
			text = label,
			style = MaterialTheme.typography.labelMedium,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
		)
	}
}

@Composable
private fun MonthNav(
	month: YearMonth,
	onPrevious: () -> Unit,
	onNext: () -> Unit,
	onToday: () -> Unit,
) {
	val label = remember(month) {
		val monthName = month.month.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault())
		"$monthName ${month.year}"
	}
	Row(
		modifier = Modifier.fillMaxWidth(),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.SpaceBetween,
	) {
		IconButton(onClick = onPrevious) {
			Icon(
				Icons.AutoMirrored.Filled.KeyboardArrowLeft,
				contentDescription = stringResource(R.string.calendar_prev_month),
			)
		}
		Row(verticalAlignment = Alignment.CenterVertically) {
			Text(
				text = label,
				style = MaterialTheme.typography.titleMedium,
			)
			TextButton(onClick = onToday) {
				Text(stringResource(R.string.calendar_today))
			}
		}
		IconButton(onClick = onNext) {
			Icon(
				Icons.AutoMirrored.Filled.KeyboardArrowRight,
				contentDescription = stringResource(R.string.calendar_next_month),
			)
		}
	}
}

@Composable
private fun MonthDayCellView(
	cell: MonthDayCell,
	onOpenDay: (LocalDate) -> Unit,
	modifier: Modifier = Modifier,
) {
	val minSize = dimensionResource(R.dimen.min_touch_target)
	val date = cell.date
	val cellModifier = if (date != null) {
		modifier
			.aspectRatio(1f)
			.padding(dimensionResource(R.dimen.spacing_xs))
			.clickable { onOpenDay(date) }
	} else {
		modifier
			.aspectRatio(1f)
			.padding(dimensionResource(R.dimen.spacing_xs))
	}
	Box(
		modifier = cellModifier,
		contentAlignment = Alignment.Center,
	) {
		if (date != null) {
			if (cell.habitProgress > 0f) {
				Box(
					modifier = Modifier
						.fillMaxSize()
						.clip(MaterialTheme.shapes.small)
						.background(
							MaterialTheme.colorScheme.tertiary.copy(
								alpha = 0.15f + 0.45f * cell.habitProgress,
							),
						),
				)
			}
			Column(horizontalAlignment = Alignment.CenterHorizontally) {
				Box(
					modifier = Modifier
						.size(minSize * 0.7f)
						.then(
							if (cell.isToday) {
								Modifier
									.clip(CircleShape)
									.background(MaterialTheme.colorScheme.primaryContainer)
							} else {
								Modifier
							},
						),
					contentAlignment = Alignment.Center,
				) {
					Text(
						text = date.dayOfMonth.toString(),
						style = MaterialTheme.typography.bodyMedium,
						color = if (cell.isToday) {
							MaterialTheme.colorScheme.onPrimaryContainer
						} else {
							MaterialTheme.colorScheme.onSurface
						},
					)
				}
				Row(
					horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_xs)),
					verticalAlignment = Alignment.CenterVertically,
				) {
					if (cell.hasBusy) {
						MarkerDot(color = MaterialTheme.colorScheme.primary)
					}
					if (cell.hasHabits) {
						MarkerDot(color = MaterialTheme.colorScheme.tertiary)
					}
				}
			}
		}
	}
}

@Composable
private fun MarkerDot(color: Color) {
	Box(
		modifier = Modifier
			.size(dimensionResource(R.dimen.calendar_marker_dot))
			.clip(CircleShape)
			.background(color),
	)
}

@Composable
private fun weekdayShort(day: DayOfWeek): String =
	day.getDisplayName(TextStyle.NARROW, Locale.getDefault())
