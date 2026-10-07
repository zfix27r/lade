package app.lade.calendar.internal.list.strip

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity
import app.lade.calendar.internal.list.strip.anim.StripSwipeGesture

internal class StripConnection(
    private val gesture: StripSwipeGesture,
    private val listState: LazyListState,
    private val fullScrollPx: Float,
) : NestedScrollConnection {

    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        if (source != NestedScrollSource.UserInput) return Offset.Zero
        val deltaY = available.y
        val progress = gesture.progress()

        if (isIntermediate(progress)) {
            gesture.onProgressDrag(deltaY, fullScrollPx)
            return Offset(0f, deltaY)
        }

        val isListAtTop = listState.firstVisibleItemIndex == 0 &&
                listState.firstVisibleItemScrollOffset == 0
        val canCollapse = deltaY < 0f && progress == 1f
        val canExpand = deltaY > 0f && progress == 0f && isListAtTop
        if (!canCollapse && !canExpand) return Offset.Zero

        gesture.onProgressDrag(deltaY, fullScrollPx)
        return Offset(0f, deltaY)
    }

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource,
    ): Offset {
        if (source == NestedScrollSource.UserInput &&
            isIntermediate(gesture.progress())
        ) {
            return available
        }
        return Offset.Zero
    }

    override suspend fun onPreFling(available: Velocity): Velocity {
        val progress = gesture.progress()
        if (!isIntermediate(progress)) return Velocity.Zero

        val target = if (progress >= 0.5f) 1f else 0f
        gesture.onProgressRelease(target)
        return available
    }

    private fun isIntermediate(progress: Float): Boolean =
        progress > 0f && progress < 1f
}