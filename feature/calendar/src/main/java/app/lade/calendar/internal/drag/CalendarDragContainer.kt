package app.lade.calendar.internal.drag

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@Composable
internal fun CalendarDragContainer(
    modifier: Modifier = Modifier,
    content: @Composable (CalendarDragStateHolder) -> Unit,
) {
    val holder = rememberDragStateHolder()
    val thresholdPx = with(LocalDensity.current) { 8.dp.toPx() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .dragGesture(
                holder = holder,
                axisLockThresholdPx = thresholdPx,
            ),
    ) {
        content(holder)
    }
}