package app.lade.calendar.internal.strip

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity
import app.lade.calendar.internal.strip.data.StripStateHolder
import app.lade.calendar.internal.strip.swipe.StripSwipeGesture

internal class StripConnection(
    private val holder: StripStateHolder,
    private val gesture: StripSwipeGesture,
    private val listState: LazyListState,
    private val fullScrollPx: Float,
) : NestedScrollConnection {

    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        if (source != NestedScrollSource.UserInput) return Offset.Zero
        val deltaY = available.y
        val progress = holder.targetProgress.floatValue

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
            isIntermediate(holder.targetProgress.floatValue)
        ) {
            return available
        }
        return Offset.Zero
    }

    override suspend fun onPreFling(available: Velocity): Velocity {
        val progress = holder.targetProgress.floatValue
        if (!isIntermediate(progress)) return Velocity.Zero
        gesture.onProgressRelease(0f)
        return available
    }

    private fun isIntermediate(progress: Float): Boolean =
        progress > 0f && progress < 1f
}