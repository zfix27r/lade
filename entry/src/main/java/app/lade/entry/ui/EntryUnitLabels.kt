package app.lade.entry.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import app.lade.entry.domain.models.EntryUnit
import app.lade.resources.R

@Composable
fun entryUnitLabel(unit: EntryUnit): String = stringResource(
	when (unit) {
		EntryUnit.KM -> R.string.habit_unit_km
		EntryUnit.MIN -> R.string.habit_unit_min
		EntryUnit.REPS -> R.string.habit_unit_reps
	},
)


