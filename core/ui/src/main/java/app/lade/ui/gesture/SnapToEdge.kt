package app.lade.ui.gesture

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs


class SnapToEdge(
    private val progress: Animatable<Float, *>,
    private val scope: CoroutineScope,
    private val haptic: HapticFeedback,
    private val config: SnapToEdgeConfig,
) {
    fun snapIfIntermediate(direction: SnapDirection): Boolean {
        val current = progress.value
        if (current <= 0f || current >= 1f) return false

        val threshold = when (direction) {
            SnapDirection.COLLAPSE -> config.collapseThreshold
            SnapDirection.EXPAND -> config.expandThreshold
        }
        val target = if (current >= threshold) 1f else 0f
        if (abs(target - current) < 0.01f) return false

        if (config.enableHaptics) {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
        scope.launch {
            progress.animateTo(
                targetValue = target,
                animationSpec = spring(
                    dampingRatio = config.dampingRatio,
                    stiffness = config.stiffness,
                ),
            )
        }
        return true
    }
}

