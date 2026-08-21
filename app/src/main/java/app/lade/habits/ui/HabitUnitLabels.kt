package app.lade.habits.ui

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import app.lade.R
import app.lade.habits.domain.model.HabitGoalPresets

@StringRes
fun habitUnitLabelRes(code: String): Int = when (code) {
	"km" -> R.string.habit_unit_km
	"min" -> R.string.habit_unit_min
	"reps" -> R.string.habit_unit_reps
	else -> R.string.habit_unit_km
}

@Composable
fun habitUnitLabel(code: String): String =
	if (code in HabitGoalPresets.UNIT_CODES) {
		stringResource(habitUnitLabelRes(code))
	} else {
		code
	}
