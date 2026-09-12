package app.lade.daypart.ui

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import app.lade.daypart.domain.DayPart
import app.lade.resources.R

@StringRes
fun DayPart.labelRes(): Int = when (this) {
    DayPart.Morning -> R.string.daypart_morning
    DayPart.Midday -> R.string.daypart_midday
    DayPart.Evening -> R.string.daypart_evening
}

@Composable
fun DayPart.label(): String = stringResource(labelRes())