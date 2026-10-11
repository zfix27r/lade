package app.lade.calendar.internal.list.strip.layout

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp

@Immutable
internal data class StripLayout(
    val activeWeekIndex: Int,
    val totalRows: Int,
    val rowHeight: Dp,
) {
    val monthHeight: Dp get() = rowHeight * totalRows
    val collapsedHeight: Dp get() = rowHeight

    fun rowColumnOffsetY(progress: Float): Dp =
        -rowHeight * activeWeekIndex * (1 - progress)

    fun listTopOffsetY(progress: Float): Dp =
        collapsedHeight + (monthHeight - collapsedHeight) * progress
}