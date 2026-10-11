package app.lade.calendar.internal.list.card.expandable

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.lade.calendardata.api.CalendarGoalExpandedModel
import kotlin.math.roundToInt

@Composable
internal fun CardExpandableGoalMeasurableRow(
    goal: CalendarGoalExpandedModel,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val planned = goal.plannedAmount ?: return
    val safeTarget = planned.coerceAtLeast(1)
    val actual = (goal.actualAmount ?: 0).coerceIn(0, planned)
    val isComplete = actual >= planned

    val step = remember(safeTarget) { adaptiveGoalStep(safeTarget) }
    val committedFraction = (actual.toFloat() / safeTarget).coerceIn(0f, 1f)

    var isDragging by remember { mutableStateOf(false) }
    var dragFraction by remember { mutableFloatStateOf(committedFraction) }

    val displayFraction = if (isDragging) dragFraction else committedFraction

    val fillColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.22f)
    val textColor = if (isComplete) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .drawBehind {
                if (displayFraction > 0f) {
                    drawRect(
                        color = fillColor,
                        topLeft = Offset.Zero,
                        size = Size(
                            width = size.width * displayFraction,
                            height = size.height,
                        ),
                    )
                }
            }
            .pointerInput(safeTarget, step) {
                awaitEachGesture {
                    val down = awaitFirstDown()
                    val width = size.width.toFloat()
                    if (width <= 0f) return@awaitEachGesture

                    val initialSnapped = snapToStep(
                        (down.position.x / width * safeTarget).roundToInt(),
                        step,
                    ).coerceIn(0, safeTarget)

                    isDragging = true
                    dragFraction = initialSnapped.toFloat() / safeTarget
                    var lastSnapped = initialSnapped

                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull { it.id == down.id }
                        if (change == null || !change.pressed) break

                        val rawFraction = (change.position.x / width).coerceIn(0f, 1f)
                        val snapped = snapToStep(
                            (rawFraction * safeTarget).roundToInt(),
                            step,
                        ).coerceIn(0, safeTarget)

                        if (snapped != lastSnapped) {
                            lastSnapped = snapped
                            dragFraction = snapped.toFloat() / safeTarget
                            onValueChange(snapped)
                        }
                        change.consume()
                    }

                    isDragging = false
                    onValueChange(lastSnapped)
                }
            }
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = goal.title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )

        Text(
            text = "$actual / $planned",
            style = MaterialTheme.typography.labelMedium,
            color = textColor,
        )
    }
}