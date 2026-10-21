package app.lade.calendar.internal.strip.swipe

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import app.lade.calendar.internal.strip.data.StripStateHolder
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlin.math.abs

@Composable
internal fun rememberStripSwipeAnimations(
    holder: StripStateHolder,
    lastVelocityX: () -> Float,
    onSettleComplete: () -> Unit,
): Pair<State<Float>, State<Float>> {
    val offsetX = remember { Animatable(0f) }
    val progress = remember { Animatable(1f) }

    LaunchedEffect(holder) {
        snapshotFlow { holder.targetOffsetX.floatValue to holder.isDragging.value }
            .distinctUntilChanged()
            .collect { (target, dragging) ->
                if (dragging) {
                    offsetX.snapTo(target)
                } else {
                    val start = offsetX.value
                    val distance = abs(target - start)
                    val duration = settleDuration(distance, lastVelocityX())
                    offsetX.animateTo(
                        targetValue = target,
                        animationSpec = tween(
                            durationMillis = duration,
                            easing = FastOutSlowInEasing,
                        ),
                    )
                    if (target != 0f) {
                        onSettleComplete()
                        offsetX.snapTo(0f)
                    }
                }
            }
    }

    LaunchedEffect(holder) {
        snapshotFlow { holder.targetProgress.floatValue to holder.isDragging.value }
            .distinctUntilChanged()
            .collect { (target, dragging) ->
                if (dragging) {
                    progress.snapTo(target)
                } else {
                    progress.animateTo(
                        targetValue = target,
                        animationSpec = tween(
                            durationMillis = 250,
                            easing = FastOutSlowInEasing,
                        ),
                    )
                }
            }
    }

    return rememberAnimatableState(offsetX) to rememberAnimatableState(progress)
}

@Composable
private fun rememberAnimatableState(animatable: Animatable<Float, *>): State<Float> =
    remember(animatable) {
        object : State<Float> {
            override val value: Float get() = animatable.value
        }
    }

private fun settleDuration(
    distance: Float,
    velocity: Float,
): Int {
    val v = abs(velocity).coerceAtLeast(100f)
    val raw = distance / v * 1000f
    return raw.coerceIn(100f, 400f).toInt()
}