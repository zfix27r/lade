package app.lade.daypart.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import app.lade.daypart.domain.DayPart
import app.lade.daypart.domain.DayPartClock
import app.lade.resources.R
import java.time.format.DateTimeFormatter


@Composable
private fun rememberDayPartTimeFmt(pattern: String): DateTimeFormatter =
    remember(pattern) { DateTimeFormatter.ofPattern(pattern) }


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DayPartChipRow(
    selected: DayPart?,
    onSelect: (DayPart) -> Unit,
    modifier: Modifier = Modifier,
    clock: DayPartClock = DayPartClock.DEFAULT,
    showTimes: Boolean = true,
    customSelected: Boolean = false,
    onCustomClick: (() -> Unit)? = null,
) {
    val timePattern = stringResource(R.string.format_time_hm)
    val timeFmt = rememberDayPartTimeFmt(timePattern)
    val gap = dimensionResource(R.dimen.spacing_sm)
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(gap),
        verticalArrangement = Arrangement.spacedBy(gap),
    ) {
        DayPart.entries.forEach { part ->
            val time = clock.timeOf(part)
            val label = if (showTimes) {
                stringResource(R.string.daypart_chip_with_time, part.label(), timeFmt.format(time))
            } else {
                part.label()
            }
            FilterChip(
                selected = selected == part && !customSelected,
                onClick = { onSelect(part) },
                label = { Text(label) },
            )
        }
        if (onCustomClick != null) {
            FilterChip(
                selected = customSelected,
                onClick = onCustomClick,
                label = { Text(stringResource(R.string.daypart_custom)) },
            )
        }
    }
}