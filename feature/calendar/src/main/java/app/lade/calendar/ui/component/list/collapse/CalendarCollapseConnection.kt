package app.lade.calendar.ui.component.list.collapse

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity
import app.lade.ui.gesture.SnapDirection
import app.lade.ui.gesture.SnapToEdge
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class CalendarCollapseConnection(
    private val progress: Animatable<Float, *>,
    private val listState: LazyListState,
    private val snapToEdge: SnapToEdge,
    private val scope: CoroutineScope,
    private val fullScrollPx: Float,
) : NestedScrollConnection {

    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        if (source != NestedScrollSource.UserInput) return Offset.Zero
        val deltaY = available.y

        if (isCollapseIntermediate()) {
            applyDelta(deltaY)
            return Offset(0f, deltaY)
        }

        val isListAtTop = listState.firstVisibleItemIndex == 0 &&
                listState.firstVisibleItemScrollOffset == 0
        val canCollapse = deltaY < 0f && progress.value == 1f
        val canExpand = deltaY > 0f && progress.value == 0f && isListAtTop
        if (!canCollapse && !canExpand) return Offset.Zero

        applyDelta(deltaY)
        return Offset(0f, deltaY)
    }

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource,
    ): Offset {
        if (source == NestedScrollSource.UserInput && isCollapseIntermediate()) {
            return available
        }
        return Offset.Zero
    }

    override suspend fun onPreFling(available: Velocity): Velocity {
        if (!isCollapseIntermediate()) return Velocity.Zero

        val direction = if (available.y < 0f) SnapDirection.COLLAPSE
        else SnapDirection.EXPAND
        snapToEdge.snapIfIntermediate(direction)
        return available
    }

    private fun isCollapseIntermediate(): Boolean =
        progress.value > 0f && progress.value < 1f

    private fun applyDelta(deltaY: Float) {
        val progressDelta = deltaY / fullScrollPx
        val next = (progress.value + progressDelta).coerceIn(0f, 1f)
        scope.launch { progress.snapTo(next) }
    }
}