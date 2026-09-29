package app.lade.goal

import androidx.annotation.StringRes

@StringRes
fun GoalUnit.shortLabelRes(): Int = when (this) {
    GoalUnit.LAP -> R.string.goal_unit_lap_short
    GoalUnit.M -> R.string.goal_unit_m_short
    GoalUnit.KM -> R.string.goal_unit_km_short
    GoalUnit.KG -> R.string.goal_unit_kg_short
    GoalUnit.MIN -> R.string.goal_unit_min_short
    GoalUnit.HOUR -> R.string.goal_unit_hour_short
    GoalUnit.SET -> R.string.goal_unit_set_short
    GoalUnit.LITER -> R.string.goal_unit_liter_short
    GoalUnit.ML -> R.string.goal_unit_ml_short
    GoalUnit.STEP -> R.string.goal_unit_step_short
    GoalUnit.GLASS -> R.string.goal_unit_glass_short
    GoalUnit.REP -> R.string.goal_unit_rep_short
    GoalUnit.APPROACH -> R.string.goal_unit_approach_short
    GoalUnit.CAL -> R.string.goal_unit_cal_short
    GoalUnit.UNKNOWN -> R.string.goal_unit_unknown_short
}