package app.lade.agenda.api.goal

import androidx.annotation.StringRes
import app.lade.agenda.R

@StringRes
fun GoalUnit.labelRes(): Int = when (this) {
    GoalUnit.LAP -> R.string.goal_unit_lap
    GoalUnit.M -> R.string.goal_unit_m
    GoalUnit.KM -> R.string.goal_unit_km
    GoalUnit.KG -> R.string.goal_unit_kg
    GoalUnit.MIN -> R.string.goal_unit_min
    GoalUnit.HOUR -> R.string.goal_unit_hour
    GoalUnit.SET -> R.string.goal_unit_set
    GoalUnit.UNKNOWN -> R.string.goal_unit_unknown
}