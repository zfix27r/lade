package app.lade.calendar.ui.component.swipe

import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import app.lade.calendar.domain.CalendarListStripMode
import app.lade.resources.R

@Composable
fun CalendarListStripSwipe(
    mode: CalendarListStripMode,
    onSwipe: (CalendarListStripMode) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val thresholdPx = with(LocalDensity.current) {
        dimensionResource(R.dimen.calendar_swipe_threshold).toPx()
    }
    var dragTotal by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(mode, thresholdPx) {
                detectVerticalDragGestures(
                    onDragEnd = {
                        when {
                            dragTotal > thresholdPx &&
                                    mode == CalendarListStripMode.MONTH ->
                                onSwipe(CalendarListStripMode.WEEK)
                            dragTotal < -thresholdPx &&
                                    mode == CalendarListStripMode.WEEK ->
                                onSwipe(CalendarListStripMode.MONTH)
                        }
                        dragTotal = 0f
                    },
                    onDragCancel = { dragTotal = 0f },
                    onVerticalDrag = { _, amount -> dragTotal += amount },
                )
            },
    ) {
        content()
    }
}