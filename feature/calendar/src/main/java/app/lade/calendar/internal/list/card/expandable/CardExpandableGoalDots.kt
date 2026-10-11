package app.lade.calendar.internal.list.card.expandable

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

private const val DOTS_LIMIT = 3

@Composable
internal fun CardExpandableGoalDots(
    total: Int,
    done: Int,
    modifier: Modifier = Modifier,
) {
    if (total <= 0) return

    val slots = total.coerceAtMost(DOTS_LIMIT)
    val filled = if (total <= DOTS_LIMIT) {
        done
    } else {
        ((done.toFloat() / total) * DOTS_LIMIT).roundToInt()
    }.coerceIn(0, slots)

    val overflow = total - DOTS_LIMIT

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        repeat(slots) { index ->
            val isDone = index < filled
            val targetColor = if (isDone) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            }
            val animatedColor by animateColorAsState(
                targetValue = targetColor,
                label = "CardExpandableGoalDotColor",
            )

            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(animatedColor),
            )
        }

        if (overflow > 0) {
            Text(
                text = "+$overflow",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}