package app.lade.calendar.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.FitnessCenter
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
import app.lade.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarCreateSheet(
	date: LocalDate,
	onDismiss: () -> Unit,
	onAddBlock: () -> Unit,
	onAddHabit: () -> Unit,
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
			ListItem(
				headlineContent = { Text(stringResource(R.string.calendar_create_block)) },
				leadingContent = {
					Icon(Icons.Default.EditCalendar, contentDescription = null)
				},
				modifier = Modifier
					.fillMaxWidth()
					.clickable {
						onDismiss()
						onAddBlock()
					},
			)
			ListItem(
				headlineContent = { Text(stringResource(R.string.calendar_create_habit)) },
				leadingContent = {
					Icon(Icons.Default.FitnessCenter, contentDescription = null)
				},
				modifier = Modifier
					.fillMaxWidth()
					.clickable {
						onDismiss()
						onAddHabit()
					},
			)
		}
	}
}
