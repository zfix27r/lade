package app.lade.calendar.internal.strip

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import app.lade.calendar.internal.strip.layout.StripLayout
import app.lade.calendar.internal.strip.layout.stripDateAt
import java.time.LocalDate
import kotlin.math.abs

@Composable
internal fun rememberTapHandler(
    weeks: List<List<LocalDate>>,
    layout: StripLayout,
    progressState: State<Float>,
    offsetXState: State<Float>,
    widthPxProvider: () -> Int,
    density: Density,
    onDateSelected: (LocalDate) -> Unit,
): (Offset) -> Unit {
    return remember(weeks, layout, density, onDateSelected) {
        { offset ->
            if (abs(offsetXState.value) <= 0.5f) {
                val widthPx = widthPxProvider()
                val columnOffsetPx = with(density) {
                    layout.rowColumnOffsetY(progressState.value).toPx()
                }
                val rowHeightPx = with(density) { layout.rowHeight.toPx() }
                val date = stripDateAt(
                    weeks = weeks,
                    x = offset.x,
                    y = offset.y,
                    widthPx = widthPx,
                    columnOffsetPx = columnOffsetPx,
                    rowHeightPx = rowHeightPx,
                )
                if (date != null) {
                    onDateSelected(date)
                }
            }
        }
    }
}