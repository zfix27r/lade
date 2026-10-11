package app.lade.calendar.internal.list.strip.port

internal interface StripGesturePort {

    val lastVelocityX: Float

    fun onDrag(deltaX: Float)
    fun onRelease(velocityX: Float)
    fun onProgressDrag(deltaY: Float, fullScrollPx: Float)
    fun onProgressRelease(velocityY: Float)
}