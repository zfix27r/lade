package app.lade.agendaui.internal

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
internal fun MiniChart(
    days: List<DayStats>,
    modifier: Modifier = Modifier,
) {
    if (days.isEmpty()) return

    val doneColor = MaterialTheme.colorScheme.primary
    val partialColor = MaterialTheme.colorScheme.tertiary
    val skippedColor = MaterialTheme.colorScheme.error
    val emptyColor = MaterialTheme.colorScheme.surfaceVariant

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp),
    ) {
        val count = days.size
        val spacing = 4f
        val barWidth = (size.width - spacing * (count - 1)) / count
        val maxBarHeight = size.height

        days.forEachIndexed { index, day ->
            val x = index * (barWidth + spacing)
            val barHeight = when {
                day.status == DayStatus.DONE -> maxBarHeight
                day.status == DayStatus.PARTIAL -> maxBarHeight * day.ratio
                day.status == DayStatus.SKIPPED -> maxBarHeight * 0.15f
                day.status == DayStatus.PLANNED -> maxBarHeight * 0.15f
                else -> maxBarHeight * 0.15f
            }
            val color = when (day.status) {
                DayStatus.DONE -> doneColor
                DayStatus.PARTIAL -> partialColor
                DayStatus.SKIPPED -> skippedColor
                DayStatus.NOT_MARKED -> skippedColor.copy(alpha = 0.4f)
                DayStatus.PLANNED -> emptyColor
                DayStatus.NO_TARGET -> emptyColor
            }
            drawRect(
                color = color,
                topLeft = Offset(x, maxBarHeight - barHeight),
                size = Size(barWidth, barHeight),
            )
        }
    }
}