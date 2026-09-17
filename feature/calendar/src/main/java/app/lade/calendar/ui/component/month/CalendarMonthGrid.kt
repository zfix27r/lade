package app.lade.calendar.ui.component.month

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import app.lade.agenda.api.agenda.AgendaModel
import app.lade.agenda.api.entry.EntryKind
import app.lade.calendar.domain.CalendarDateMode
import app.lade.calendar.ui.component.swipe.CalendarDateSwipe
import app.lade.resources.R
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.time.temporal.WeekFields

@Composable
fun CalendarMonthGrid(
	month: YearMonth,
	entries: List<AgendaModel>,
	selectedDate: LocalDate,
	onSwipe: (CalendarDateMode) -> Unit,
	onOpenDay: (LocalDate) -> Unit,
	modifier: Modifier = Modifier,
) {
	val today = remember { LocalDate.now() }
	val firstDayOfWeek = WeekFields.of(LocalLocale.current.platformLocale).firstDayOfWeek
	val weekdays = (0..6).map { firstDayOfWeek.plus(it.toLong()) }
	val lead = ((month.atDay(1).dayOfWeek.value - firstDayOfWeek.value + 7) % 7)
	val cells = buildList<LocalDate?> {
		repeat(lead) { add(null) }
		for (day in 1..month.lengthOfMonth()) add(month.atDay(day))
		while (size % 7 != 0) add(null)
	}
	val datesWithEntries = entries.map { it.date }.toSet()
	val habitDates = entries
		.filter { it.entry.kind == EntryKind.HABIT }
		.map { it.date }
		.toSet()

	CalendarDateSwipe(
		onSwipe = onSwipe,
		modifier = modifier.fillMaxSize(),
	) {
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(horizontal = dimensionResource(R.dimen.screen_padding)),
		) {
			Row(modifier = Modifier.fillMaxWidth()) {
				weekdays.forEach { day ->
					Text(
						text = day.getDisplayName(TextStyle.NARROW, LocalLocale.current.platformLocale),
						style = MaterialTheme.typography.labelMedium,
						color = MaterialTheme.colorScheme.onSurfaceVariant,
						textAlign = TextAlign.Center,
						modifier = Modifier.weight(1f),
					)
				}
			}
			cells.chunked(7).forEach { week ->
				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_xs)),
				) {
					week.forEach { date ->
						MonthDayCellView(
							date = date,
							isToday = date == today,
							isSelected = date == selectedDate,
							hasEntries = date != null && date in datesWithEntries,
							hasHabits = date != null && date in habitDates,
							onOpenDay = onOpenDay,
							modifier = Modifier.weight(1f),
						)
					}
				}
			}
		}
	}
}

@Composable
private fun MonthDayCellView(
	date: LocalDate?,
	isToday: Boolean,
	isSelected: Boolean,
	hasEntries: Boolean,
	hasHabits: Boolean,
	onOpenDay: (LocalDate) -> Unit,
	modifier: Modifier = Modifier,
) {
	val minSize = dimensionResource(R.dimen.min_touch_target)
	Box(
		modifier = modifier
			.aspectRatio(1f)
			.padding(dimensionResource(R.dimen.spacing_xs))
			.then(
				if (date != null) {
					Modifier.clickable { onOpenDay(date) }
				} else {
					Modifier
				},
			),
		contentAlignment = Alignment.Center,
	) {
		if (date != null) {
			Column(horizontalAlignment = Alignment.CenterHorizontally) {
				Box(
					modifier = Modifier
						.size(minSize * 0.7f)
						.then(
							when {
								isSelected -> Modifier
									.clip(CircleShape)
									.background(MaterialTheme.colorScheme.primary)
								isToday -> Modifier
									.clip(CircleShape)
									.background(MaterialTheme.colorScheme.primaryContainer)
								else -> Modifier
							},
						),
					contentAlignment = Alignment.Center,
				) {
					Text(
						text = date.dayOfMonth.toString(),
						style = MaterialTheme.typography.bodyMedium,
						color = when {
							isSelected -> MaterialTheme.colorScheme.onPrimary
							isToday -> MaterialTheme.colorScheme.onPrimaryContainer
							else -> MaterialTheme.colorScheme.onSurface
						},
					)
				}
				Row(
					horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_xs)),
					verticalAlignment = Alignment.CenterVertically,
				) {
					if (hasEntries) {
						MarkerDot(color = MaterialTheme.colorScheme.primary)
					}
					if (hasHabits) {
						MarkerDot(color = MaterialTheme.colorScheme.tertiary)
					}
				}
			}
		}
	}
}

@Composable
private fun MarkerDot(color: androidx.compose.ui.graphics.Color) {
	Box(
		modifier = Modifier
			.size(dimensionResource(R.dimen.calendar_marker_dot))
			.clip(CircleShape)
			.background(color),
	)
}