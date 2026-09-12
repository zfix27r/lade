package app.lade.calendar.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import app.lade.resources.R
import app.lade.entry.domain.models.EntryKind
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

/**
 * Единый жест создания с календаря: короткая развилка kind → Entry edit с дефолтами.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarCreateSheet(
	date: LocalDate,
	onDismiss: () -> Unit,
	onCreate: (EntryKind) -> Unit,
) {
	val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
	val dateFmt = remember {
		DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.getDefault())
	}
	ModalBottomSheet(
		onDismissRequest = onDismiss,
		sheetState = sheetState,
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(bottom = dimensionResource(R.dimen.spacing_xl)),
		) {
			Text(
				text = stringResource(R.string.calendar_create_sheet_title),
				style = MaterialTheme.typography.titleLarge,
				modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.screen_padding)),
			)
			Text(
				text = stringResource(R.string.calendar_create_for_date, date.format(dateFmt)),
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
				modifier = Modifier.padding(
					horizontal = dimensionResource(R.dimen.screen_padding),
					vertical = dimensionResource(R.dimen.spacing_sm),
				),
			)
			Text(
				text = stringResource(R.string.calendar_create_hint),
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
				modifier = Modifier.padding(
					horizontal = dimensionResource(R.dimen.screen_padding),
					vertical = dimensionResource(R.dimen.spacing_sm),
				),
			)
			CreateKindRow(
				title = stringResource(R.string.calendar_create_task),
				hint = stringResource(R.string.calendar_create_task_hint),
				icon = { Icon(Icons.Default.TaskAlt, contentDescription = null) },
				onClick = {
					onDismiss()
					onCreate(EntryKind.TASK)
				},
			)
			CreateKindRow(
				title = stringResource(R.string.calendar_create_event),
				hint = stringResource(R.string.calendar_create_event_hint),
				icon = { Icon(Icons.Default.Event, contentDescription = null) },
				onClick = {
					onDismiss()
					onCreate(EntryKind.EVENT)
				},
			)
			CreateKindRow(
				title = stringResource(R.string.calendar_create_habit_entry),
				hint = stringResource(R.string.calendar_create_habit_entry_hint),
				icon = { Icon(Icons.Default.FitnessCenter, contentDescription = null) },
				onClick = {
					onDismiss()
					onCreate(EntryKind.HABIT)
				},
			)
			CreateKindRow(
				title = stringResource(R.string.calendar_create_schedule),
				hint = stringResource(R.string.calendar_create_schedule_hint),
				icon = { Icon(Icons.Default.Schedule, contentDescription = null) },
				onClick = {
					onDismiss()
					onCreate(EntryKind.SCHEDULE)
				},
			)
		}
	}
}

@Composable
private fun CreateKindRow(
	title: String,
	hint: String,
	icon: @Composable () -> Unit,
	onClick: () -> Unit,
) {
	ListItem(
		headlineContent = { Text(title) },
		supportingContent = { Text(hint) },
		leadingContent = icon,
		modifier = Modifier
			.fillMaxWidth()
			.clickable(onClick = onClick),
	)
}
