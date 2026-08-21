package app.lade.calendar.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.lade.R
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.TextStyle
import java.util.Locale

private const val GRID_HOUR_FROM = 6
private const val GRID_HOUR_TO = 22

@Composable
fun CalendarWeekTimeGrid(
	days: List<WeekDayRow>,
	onOpenDay: (LocalDate) -> Unit,
	modifier: Modifier = Modifier,
) {
	val hourHeight = dimensionResource(R.dimen.calendar_week_grid_hour_height)
	val hourLabelWidth = dimensionResource(R.dimen.calendar_week_grid_hour_label)
	val dayColWidth = 72.dp
	val hours = GRID_HOUR_FROM until GRID_HOUR_TO

	Column(
		modifier = modifier
			.fillMaxSize()
			.horizontalScroll(rememberScrollState())
			.verticalScroll(rememberScrollState())
			.padding(horizontal = dimensionResource(R.dimen.spacing_sm)),
	) {
		Row(verticalAlignment = Alignment.Bottom) {
			Box(modifier = Modifier.width(hourLabelWidth))
			days.forEach { day ->
				val weekday = day.date.dayOfWeek.getDisplayName(TextStyle.NARROW, Locale.getDefault())
				Text(
					text = "$weekday\n${day.date.dayOfMonth}",
					style = MaterialTheme.typography.labelSmall,
					textAlign = TextAlign.Center,
					color = when {
						day.isSelected -> MaterialTheme.colorScheme.primary
						day.isToday -> MaterialTheme.colorScheme.tertiary
						else -> MaterialTheme.colorScheme.onSurfaceVariant
					},
					modifier = Modifier
						.width(dayColWidth)
						.clickable { onOpenDay(day.date) }
						.padding(bottom = dimensionResource(R.dimen.spacing_xs)),
				)
			}
		}
		hours.forEach { hour ->
			Row(
				modifier = Modifier.height(hourHeight),
				verticalAlignment = Alignment.Top,
			) {
				Text(
					text = "%02d".format(hour),
					style = MaterialTheme.typography.labelSmall,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
					modifier = Modifier
						.width(hourLabelWidth)
						.padding(end = dimensionResource(R.dimen.spacing_xs)),
				)
				days.forEach { day ->
					val covering = day.dayBusy.intervals.filter { interval ->
						coversHour(interval.start, interval.end, hour)
					}
					Box(
						modifier = Modifier
							.width(dayColWidth)
							.fillMaxHeight()
							.border(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
							.clickable { onOpenDay(day.date) }
							.padding(1.dp),
					) {
						if (covering.isNotEmpty()) {
							Box(
								modifier = Modifier
									.fillMaxSize()
									.background(MaterialTheme.colorScheme.primaryContainer),
								contentAlignment = Alignment.Center,
							) {
								Text(
									text = covering.first().title.ifBlank { "·" },
									style = MaterialTheme.typography.labelSmall,
									color = MaterialTheme.colorScheme.onPrimaryContainer,
									maxLines = 1,
									overflow = TextOverflow.Ellipsis,
								)
							}
						}
					}
				}
			}
		}
	}
}

private fun coversHour(start: LocalTime, end: LocalTime, hour: Int): Boolean {
	val hourStart = hour * 60
	val hourEnd = hourStart + 60
	val startMin = start.hour * 60 + start.minute
	val endMin = end.hour * 60 + end.minute
	return startMin < hourEnd && endMin > hourStart
}
