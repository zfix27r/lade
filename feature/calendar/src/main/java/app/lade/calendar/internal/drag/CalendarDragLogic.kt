package app.lade.calendar.internal.drag

import androidx.compose.ui.input.pointer.PointerEventPass
import kotlin.math.abs

internal sealed interface CalendarDragResult {
    data class Settle(val velocity: Float) : CalendarDragResult
    data object Tap : CalendarDragResult
    data object Cancel : CalendarDragResult
}

internal data class CalendarDragGestureConfig(
    val axisLockThresholdPx: Float,
    val tapTimeoutMs: Long = 200L,
    val consumeVertical: Boolean = true,
    val consumeHorizontal: Boolean = true,
    val eventPass: PointerEventPass = PointerEventPass.Main,
)

internal data class CalendarDragMoveDelta(
    val dx: Float,
    val dy: Float,
)

internal fun resolveDragAxis(
    totalX: Float,
    totalY: Float,
    thresholdPx: Float,
    horizontalEnabled: Boolean,
    verticalEnabled: Boolean,
): CalendarDragDirection? = when {
    !horizontalEnabled && !verticalEnabled -> null
    !horizontalEnabled ->
        if (abs(totalY) > thresholdPx) CalendarDragDirection.VERTICAL else null
    !verticalEnabled ->
        if (abs(totalX) > thresholdPx) CalendarDragDirection.HORIZONTAL else null
    abs(totalX) <= thresholdPx && abs(totalY) <= thresholdPx -> null
    abs(totalY) > abs(totalX) -> CalendarDragDirection.VERTICAL
    else -> CalendarDragDirection.HORIZONTAL
}

internal fun applyDragOffset(
    direction: CalendarDragDirection,
    baseOffset: Float,
    totalX: Float,
    totalY: Float,
    horizontal: CalendarDragBehavior,
    vertical: CalendarDragBehavior,
): Float = when (direction) {
    CalendarDragDirection.HORIZONTAL -> horizontal.clamp(baseOffset + totalX)
    CalendarDragDirection.VERTICAL -> vertical.clamp(baseOffset + totalY)
    CalendarDragDirection.NONE -> baseOffset
}

internal fun resolveDragResult(
    locked: Boolean,
    wasSettling: Boolean,
    durationMs: Long,
    tapTimeoutMs: Long,
    velocity: Float,
): CalendarDragResult = when {
    locked -> CalendarDragResult.Settle(velocity)
    wasSettling -> CalendarDragResult.Settle(0f)
    durationMs <= tapTimeoutMs -> CalendarDragResult.Tap
    else -> CalendarDragResult.Cancel
}

internal class CalendarDragGestureTracker(
    private val horizontal: CalendarDragBehavior,
    private val vertical: CalendarDragBehavior,
    private val thresholdPx: Float,
) {
    var direction: CalendarDragDirection = CalendarDragDirection.NONE
        private set

    private var lastX: Float = 0f
    private var lastY: Float = 0f
    private var totalX: Float = 0f
    private var totalY: Float = 0f
    private var baseOffset: Float = 0f
    private var downTime: Long = 0L

    val isLocked: Boolean get() = direction != CalendarDragDirection.NONE

    fun begin(x: Float, y: Float, uptime: Long) {
        lastX = x
        lastY = y
        totalX = 0f
        totalY = 0f
        baseOffset = 0f
        direction = CalendarDragDirection.NONE
        downTime = uptime
    }

    fun moveTo(x: Float, y: Float): CalendarDragMoveDelta {
        val dx = x - lastX
        val dy = y - lastY
        lastX = x
        lastY = y
        return CalendarDragMoveDelta(dx = dx, dy = dy)
    }

    fun accumulate(delta: CalendarDragMoveDelta) {
        totalX += delta.dx
        totalY += delta.dy
    }

    fun lockIfNeeded(holder: CalendarDragStateHolder): Boolean {
        if (isLocked) return false
        val resolved = resolveDragAxis(
            totalX = totalX,
            totalY = totalY,
            thresholdPx = thresholdPx,
            horizontalEnabled = horizontal.enabled,
            verticalEnabled = vertical.enabled,
        ) ?: return false
        direction = resolved
        holder.lock(resolved)
        baseOffset = holder.state.value.offset
        return true
    }

    fun currentOffset(): Float =
        applyDragOffset(direction, baseOffset, totalX, totalY, horizontal, vertical)

    fun shouldConsume(
        consumeHorizontal: Boolean,
        consumeVertical: Boolean,
    ): Boolean = when (direction) {
        CalendarDragDirection.HORIZONTAL -> consumeHorizontal
        CalendarDragDirection.VERTICAL -> consumeVertical
        CalendarDragDirection.NONE -> false
    }

    fun finish(
        uptime: Long,
        wasSettling: Boolean,
        tapTimeoutMs: Long,
        velocity: Float,
    ): CalendarDragResult = resolveDragResult(
        locked = isLocked,
        wasSettling = wasSettling,
        durationMs = uptime - downTime,
        tapTimeoutMs = tapTimeoutMs,
        velocity = velocity,
    )
}