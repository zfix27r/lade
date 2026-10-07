package app.lade.calendar.internal.list.strip

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import app.lade.calendar.api.config.StripConfig
import app.lade.calendar.internal.list.strip.anim.StripController
import app.lade.calendar.internal.list.strip.anim.StripSwipeAnimator
import app.lade.calendar.internal.list.strip.anim.StripSwipeGesture
import app.lade.calendar.internal.list.strip.data.StripStateHolder

@Composable
internal fun rememberStripController(
    holder: StripStateHolder,
    config: StripConfig,
): StripController {
    val scope = rememberCoroutineScope()
    val animator = remember(holder) { StripSwipeAnimator(holder) }
    val gesture = remember(holder, animator, config) {
        StripSwipeGesture(holder, animator, config)
    }
    LaunchedEffect(animator, scope) {
        animator.attach(scope)
    }
    return remember(gesture, holder) { StripController(gesture, holder) }
}