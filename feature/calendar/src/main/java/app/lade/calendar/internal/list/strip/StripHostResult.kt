package app.lade.calendar.internal.list.strip

import androidx.compose.ui.unit.Dp
import app.lade.calendar.internal.list.strip.port.StripListPort
import app.lade.calendar.internal.list.strip.port.StripOutsidePort

internal class StripHostResult(
    val listPort: StripListPort,
    val outsidePort: StripOutsidePort,
    val topOffset: () -> Dp,
)