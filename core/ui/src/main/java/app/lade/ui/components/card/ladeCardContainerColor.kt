package app.lade.ui.components.card

import androidx.compose.animation.animateColorAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color

@Composable
fun ladeCardContainerColor(isCompleted: Boolean): Color {
    val cardColors = LocalLadeCardColors.current
    val color by animateColorAsState(
        targetValue = if (isCompleted) {
            cardColors.completedContainer
        } else {
            cardColors.activeContainer
        },
        label = "LadeCardContainer",
    )
    return color
}