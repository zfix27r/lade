package app.lade.calendar.ui.component.swipe

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import app.lade.calendar.domain.CalendarDateMode
import app.lade.calendar.domain.CalendarListStripMode
import app.lade.resources.R

@Composable
fun CalendarListStripSwipe(
    mode: CalendarListStripMode,
    onHorizontal: (CalendarDateMode) -> Unit,
    onVertical: (CalendarListStripMode) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val thresholdPx = with(LocalDensity.current) {
        dimensionResource(R.dimen.calendar_swipe_threshold).toPx()
    }
    var verticalTotal by remember { mutableFloatStateOf(0f) }
    var horizontalTotal by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(mode, thresholdPx) {
                detectVerticalDragGestures(
                    onDragEnd = {
                        when {
                            verticalTotal > thresholdPx &&
                                    mode == CalendarListStripMode.MONTH ->
                                onVertical(CalendarListStripMode.WEEK)
                            verticalTotal < -thresholdPx &&
                                    mode == CalendarListStripMode.WEEK ->
                                onVertical(CalendarListStripMode.MONTH)
                        }
                        verticalTotal = 0f
                    },
                    onDragCancel = { verticalTotal = 0f },
                    onVerticalDrag = { _, amount -> verticalTotal += amount },
                )
            }
            .pointerInput(thresholdPx) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (horizontalTotal > thresholdPx) {
                            onHorizontal(CalendarDateMode.BACKWARD)
                        } else if (horizontalTotal < -thresholdPx) {
                            onHorizontal(CalendarDateMode.FORWARD)
                        }
                        horizontalTotal = 0f
                    },
                    onDragCancel = { horizontalTotal = 0f },
                    onHorizontalDrag = { _, amount -> horizontalTotal += amount },
                )
            },
    ) {
        content()
    }
}