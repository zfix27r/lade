package app.lade.calendar.internal.week

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import app.lade.calendar.internal.drag.CalendarDragPhase
import app.lade.calendar.internal.drag.CalendarDragStateHolder
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
internal fun rememberWeekVerticalFling(
    holder: CalendarDragStateHolder,
    maxOffsetPx: Float,
    velocityThresholdPx: Float = 800f,
): Animatable<Float, AnimationVector1D> {
    val fling = remember { Animatable(0f) }
    val currentMax by rememberUpdatedState(maxOffsetPx)

    LaunchedEffect(holder) {
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
                val max = currentMax.coerceAtLeast(0f)
                val current = holder.currentOffset.coerceIn(0f, max)
                fling.snapTo(current)

                // Если позиция вне диапазона — догоняем до ближайшей границы.
                // Внутри диапазона — только инерция, без насильного «доехать до края».
                val outOfRange = holder.currentOffset < 0f || holder.currentOffset > max
                val target = when {
                    outOfRange -> current
                    abs(v) < velocityThresholdPx -> current
                    v < 0f -> max
                    else -> 0f
                }

                if (target != current || outOfRange) {
                    fling.animateTo(
                        targetValue = target,
                        initialVelocity = if (outOfRange) 0f else v,
                        animationSpec = spring(
                            stiffness = 400f,
                            dampingRatio = 0.85f,
                            visibilityThreshold = 0.5f,
                        ),
                    ) {
                        holder.drag(value)
                    }
                }

                holder.cancel()
            } catch (_: CancellationException) {
                // прервано новым жестом
            }
        }
    }

    return fling
}