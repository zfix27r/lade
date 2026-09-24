package app.lade.ui.tokens.colors

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import app.lade.ui.R
import androidx.core.graphics.toColorInt

@Composable
fun categoryColor(colorKey: String): Color {
    if (colorKey.startsWith("#")) {
        return runCatching {
            Color(colorKey.toColorInt())
        }.getOrElse {
            colorResource(R.color.category_slate)
        }
    }

    // Фиксированный набор
    val resId = when (colorKey) {
        "slate"   -> R.color.category_slate_light
        "stone"   -> R.color.category_stone_light
        "brown"   -> R.color.category_brown_light
        "red"     -> R.color.category_red_light
        "rose"    -> R.color.category_rose_light
        "orange"  -> R.color.category_orange_light
        "amber"   -> R.color.category_amber_light
        "yellow"  -> R.color.category_yellow_light
        "lime"    -> R.color.category_lime_light
        "green"   -> R.color.category_green_light
        "emerald" -> R.color.category_emerald_light
        "teal"    -> R.color.category_teal_light
        "cyan"    -> R.color.category_cyan_light
        "sky"     -> R.color.category_sky_light
        "blue"    -> R.color.category_blue_light
        "indigo"  -> R.color.category_indigo_light
        "violet"  -> R.color.category_violet_light
        "purple"  -> R.color.category_purple_light
        "fuchsia" -> R.color.category_fuchsia_light
        "pink"    -> R.color.category_pink_light
        else      -> R.color.category_slate_light
    }
    return colorResource(resId)
}