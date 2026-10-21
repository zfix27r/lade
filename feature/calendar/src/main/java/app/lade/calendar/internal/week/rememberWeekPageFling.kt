package app.lade.calendar.internal.week

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import app.lade.calendar.internal.drag.CalendarDragPhase
import app.lade.calendar.internal.drag.CalendarDragStateHolder
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * Горизонтальный fling для страничной навигации по неделям.
 * Целевая позиция — кратная [pageWidthPx] (или 0, если порог не превышен).
 */
@Composable
internal fun rememberWeekPageFling(
    holder: CalendarDragStateHolder,
    pageWidthPx: Float,
    velocityThresholdPx: Float = 800f,
    onSettled: (Float) -> Unit,
): Animatable<Float, AnimationVector1D> {
    val fling = remember { Animatable(0f) }

    LaunchedEffect(holder, pageWidthPx, velocityThresholdPx) {
        launch {
            holder.state
                .map { it.phase }
                .distinctUntilChanged()
                .collect { phase ->
                    if (phase != CalendarDragPhase.SETTLED) fling.stop()
                }
        }

        holder.settleEvents.collect { v ->
            try {
                fling.snapTo(holder.currentOffset)
                val current = fling.value

                val target = if (abs(v) > velocityThresholdPx || abs(current) > pageWidthPx / 2f) {
                    if (current < 0f) -pageWidthPx else pageWidthPx
                } else 0f

                fling.animateTo(
                    targetValue = target,
                    initialVelocity = v,
                    animationSpec = spring(
                        stiffness = 400f,
                        dampingRatio = 0.85f,
                        visibilityThreshold = 0.5f,
                    ),
                ) {
                    holder.drag(value)
                }

                if (target != 0f) onSettled(target)
                holder.cancel()
            } catch (_: CancellationException) {
                // fling прерван новым жестом — холдер уже содержит позицию
            }
        }
    }

    return fling
}