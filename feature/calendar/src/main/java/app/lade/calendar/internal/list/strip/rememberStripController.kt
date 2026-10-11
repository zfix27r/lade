package app.lade.calendar.internal.list.strip

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import app.lade.calendar.api.config.StripConfig
import app.lade.calendar.internal.list.strip.data.StripStateHolder
import app.lade.calendar.internal.list.strip.swipe.StripSwipeGesture

@Composable
internal fun rememberStripGestures(
    holder: StripStateHolder,
    config: StripConfig,
): StripSwipeGesture = remember(holder, config) {
    StripSwipeGesture(holder, config)
}