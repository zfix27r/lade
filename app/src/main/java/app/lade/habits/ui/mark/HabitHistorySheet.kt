package app.lade.habits.ui.mark

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import app.lade.R
import app.lade.habits.domain.HabitDayMarkKind
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitHistorySheet(
	habitTitle: String,
	month: YearMonth,
	marksByEpochDay: Map<Long, HabitDayMarkKind>,
	onDismiss: () -> Unit,
) {
	val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
	val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
	val weekdays = remember(firstDayOfWeek) {
		(0..6).map { firstDayOfWeek.plus(it.toLong()) }
	}
	val cells = remember(month, firstDayOfWeek) { buildMonthCells(month, firstDayOfWeek) }
	val monthLabel = remember(month) {
		val name = month.month.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault())
		"$name ${month.year}"
	}

	ModalBottomSheet(
		onDismissRequest = onDismiss,
		sheetState = sheetState,
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = dimensionResource(R.dimen.screen_padding))
				.padding(bottom = dimensionResource(R.dimen.spacing_xl)),
		) {
			Text(
				text = stringResource(R.string.habit_history_sheet_title, habitTitle),
				style = MaterialTheme.typography.titleLarge,
			)
			Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_sm)))
			Text(
				text = monthLabel,
				style = MaterialTheme.typography.titleSmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
			)
			Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_md)))
			Row(modifier = Modifier.fillMaxWidth()) {
				weekdays.forEach { day ->
					Text(
						text = day.getDisplayName(TextStyle.NARROW, Locale.getDefault()),
						style = MaterialTheme.typography.labelMedium,
						color = MaterialTheme.colorScheme.onSurfaceVariant,
						modifier = Modifier.weight(1f),
						textAlign = androidx.compose.ui.text.style.TextAlign.Center,
					)
				}
			}
			Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_sm)))
			cells.chunked(7).forEach { week ->
				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_xs)),
				) {
					week.forEach { date ->
						Box(
							modifier = Modifier
								.weight(1f)
								.aspectRatio(1f)
								.padding(dimensionResource(R.dimen.spacing_xs)),
							contentAlignment = Alignment.Center,
						) {
							if (date != null) {
								val kind = marksByEpochDay[date.toEpochDay()] ?: HabitDayMarkKind.NONE
								Column(horizontalAlignment = Alignment.CenterHorizontally) {
									Text(
										text = date.dayOfMonth.toString(),
										style = MaterialTheme.typography.bodySmall,
									)
									Box(
										modifier = Modifier
											.size(dimensionResource(R.dimen.calendar_marker_dot))
											.clip(CircleShape)
											.background(markColor(kind)),
									)
								}
							}
						}
					}
				}
			}
		}
	}
}

@Composable
private fun markColor(kind: HabitDayMarkKind): Color = when (kind) {
	HabitDayMarkKind.DONE -> MaterialTheme.colorScheme.primary
	HabitDayMarkKind.SKIPPED -> MaterialTheme.colorScheme.secondary
	HabitDayMarkKind.MISSED -> MaterialTheme.colorScheme.error
	HabitDayMarkKind.PLANNED -> MaterialTheme.colorScheme.outline
	HabitDayMarkKind.NONE -> Color.Transparent
}

private fun buildMonthCells(month: YearMonth, firstDayOfWeek: DayOfWeek): List<LocalDate?> {
	val first = month.atDay(1)
	val lead = ((first.dayOfWeek.value - firstDayOfWeek.value + 7) % 7)
	val cells = ArrayList<LocalDate?>(42)
	repeat(lead) { cells += null }
	for (day in 1..month.lengthOfMonth()) {
		cells += month.atDay(day)
	}
	while (cells.size % 7 != 0) {
		cells += null
	}
	return cells
}
