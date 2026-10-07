package app.lade.calendar.internal.list.strip.anim

import app.lade.calendar.internal.list.strip.data.StripStateHolder

internal class StripController internal constructor(
    val gesture: StripSwipeGesture,
    private val holder: StripStateHolder,
) {
    fun onWidthChanged(px: Int) = holder.setWidthPx(px)
}

