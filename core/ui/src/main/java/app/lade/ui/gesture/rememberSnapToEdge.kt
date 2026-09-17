package app.lade.ui.gesture

import androidx.compose.animation.core.Animatable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalHapticFeedback

@Composable
fun rememberSnapToEdge(
    progress: Animatable<Float, *>,
    config: SnapToEdgeConfig,
): SnapToEdge {
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    return remember(progress, scope, haptic, config) {
        SnapToEdge(progress, scope, haptic, config)
    }
}