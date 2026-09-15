package app.lade.calendar.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.lade.calendar.domain.CalendarBusyInterval
import app.lade.resources.R

import java.time.format.DateTimeFormatter
import kotlin.math.max

@Composable
fun CalendarDayTimeline(
	intervals: List<CalendarBusyInterval>,
	onIntervalClick: (CalendarBusyInterval) -> Unit,
	modifier: Modifier = Modifier,
) {
	val timePattern = stringResource(R.string.format_time_hm)
	val timeFmt = remember(timePattern) { DateTimeFormatter.ofPattern(timePattern) }
	val hourHeight = dimensionResource(R.dimen.calendar_timeline_hour_height)
	val density = LocalDensity.current
	val hourHeightPx = with(density) { hourHeight.toPx() }
	val labelWidth = dimensionResource(R.dimen.calendar_week_grid_hour_label)
	val totalHeight = hourHeight * 24

	Box(
		modifier = modifier
			.fillMaxSize()
			.verticalScroll(rememberScrollState())
			.padding(horizontal = dimensionResource(R.dimen.screen_padding)),
	) {
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.height(totalHeight),
		) {
			Column(modifier = Modifier.fillMaxSize()) {
				repeat(24) { hour ->
					Row(
						modifier = Modifier
							.fillMaxWidth()
							.height(hourHeight)
							.border(
								width = 0.5.dp,
								color = MaterialTheme.colorScheme.outlineVariant,
							),
					) {
						Text(
							text = String.format("%02d:00", hour),
							style = MaterialTheme.typography.labelSmall,
							color = MaterialTheme.colorScheme.onSurfaceVariant,
							modifier = Modifier
								.padding(end = dimensionResource(R.dimen.spacing_sm))
								.padding(top = dimensionResource(R.dimen.spacing_xs)),
						)
					}
				}
			}
			intervals.forEach { interval ->
				val startMin = interval.start.hour * 60 + interval.start.minute
				val endMin = max(startMin + 15, interval.end.hour * 60 + interval.end.minute)
				val topPx = startMin / 60f * hourHeightPx
				val heightPx = (endMin - startMin) / 60f * hourHeightPx
				val editable = interval.entryId > 0L
				Box(
					modifier = Modifier
						.offset(
							x = labelWidth,
							y = with(density) { topPx.toDp() },
						)
						.padding(end = dimensionResource(R.dimen.spacing_sm))
						.fillMaxWidth()
						.height(
							with(density) { heightPx.toDp() }
								.coerceAtLeast(dimensionResource(R.dimen.spacing_lg)),
						)
						.background(
							color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
							shape = MaterialTheme.shapes.small,
						)
						.then(
							if (editable) {
								Modifier.clickable { onIntervalClick(interval) }
							} else {
								Modifier
							},
						)
						.padding(dimensionResource(R.dimen.spacing_xs)),
				) {
					Text(
						text = stringResource(
							R.string.format_time_range,
							interval.start.format(timeFmt),
							interval.end.format(timeFmt),
						) + " · " + interval.title.ifBlank {
							stringResource(R.string.time_block_untitled)
						},
						style = MaterialTheme.typography.labelMedium,
						color = MaterialTheme.colorScheme.onPrimaryContainer,
						maxLines = 2,
					)
				}
			}
		}
	}
}
