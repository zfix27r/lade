package app.lade.ui.tokens.colors

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import app.lade.resources.R

@Composable
fun categoryColor(colorKey: String): Color {
    if (colorKey.startsWith("#")) {
        return runCatching { Color(android.graphics.Color.parseColor(colorKey)) }
            .getOrElse { colorResource(R.color.category_slate) }
    }
    val resId = when (colorKey) {
        "slate" -> R.color.category_slate
        "stone" -> R.color.category_stone
        "brown" -> R.color.category_brown
        "red" -> R.color.category_red
        "rose" -> R.color.category_rose
        "orange" -> R.color.category_orange
        "amber" -> R.color.category_amber
        "yellow" -> R.color.category_yellow
        "lime" -> R.color.category_lime
        "green" -> R.color.category_green
        "emerald" -> R.color.category_emerald
        "teal" -> R.color.category_teal
        "cyan" -> R.color.category_cyan
        "sky" -> R.color.category_sky
        "blue" -> R.color.category_blue
        "indigo" -> R.color.category_indigo
        "violet" -> R.color.category_violet
        "purple" -> R.color.category_purple
        "fuchsia" -> R.color.category_fuchsia
        "pink" -> R.color.category_pink
        else -> R.color.category_slate
    }
    return colorResource(resId)
}
