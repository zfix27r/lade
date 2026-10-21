package app.lade.calendar.internal.drag

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope

@Composable
internal fun rememberDragStateHolder(): CalendarDragStateHolder {
    val scope = rememberCoroutineScope()
    val holder = remember(scope) { CalendarDragStateHolder(scope) }
    DisposableEffect(holder) {
        onDispose { holder.dispose() }
    }
    return holder
}