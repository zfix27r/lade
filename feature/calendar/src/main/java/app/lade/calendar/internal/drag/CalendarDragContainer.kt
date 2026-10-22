package app.lade.calendar.internal.drag

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
internal fun CalendarDragContainer(
    modifier: Modifier = Modifier,
    horizontal: CalendarDragBehavior = CalendarDragBehavior.disabled,
    vertical: CalendarDragBehavior = CalendarDragBehavior.disabled,
    config: CalendarDragGestureConfig = CalendarDragGestureConfig(axisLockThresholdPx = 0f),
    content: @Composable (CalendarDragStateHolder) -> Unit,
) {
    val holder = remember { CalendarDragStateHolder() }

    SideEffect {
        holder.updateAxes(horizontal, vertical)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .dragGesture(holder = holder, config = config),
    ) {
        content(holder)
    }
}