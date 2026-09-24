package app.lade.schedule.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import app.lade.resources.R
import app.lade.recurrence.api.DaysOfWeekFlags

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DaysOfWeekSelector(
	flags: Int,
	onFlagsChange: (Int) -> Unit,
	modifier: Modifier = Modifier,
) {
	val gap = dimensionResource(R.dimen.spacing_sm)
	FlowRow(
		modifier = modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(gap),
		verticalArrangement = Arrangement.spacedBy(gap),
	) {
		weekdayLabelRes.forEach { (day, labelRes) ->
			FilterChip(
				selected = DaysOfWeekFlags.has(flags, day),
				onClick = { onFlagsChange(DaysOfWeekFlags.toggle(flags, day)) },
				label = { Text(stringResource(labelRes)) },
			)
		}
	}
}
