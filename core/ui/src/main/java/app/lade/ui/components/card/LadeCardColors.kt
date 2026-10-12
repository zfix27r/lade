package app.lade.ui.components.card

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class LadeCardColors(
    val completedContainer: Color,
    val activeContainer: Color,
)

val LocalLadeCardColors = staticCompositionLocalOf {
    LadeCardColors(
        completedContainer = Color.Unspecified,
        activeContainer = Color.Unspecified,
    )
}