package app.lade.calendar.internal.list.strip.port

import androidx.compose.runtime.State

internal interface StripScrollPort {

    val progress: State<Float>

    fun onScroll(deltaY: Float, fullScrollPx: Float): Float
    fun onStop()
}