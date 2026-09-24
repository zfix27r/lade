package app.lade.ui.theme

import android.provider.Settings
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

val LocalMotionScale = compositionLocalOf { 1f }

@Composable
fun rememberAnimationScale(): Float {
    val context = LocalContext.current
    return remember {
        Settings.Global.getFloat(
            context.contentResolver,
            Settings.Global.ANIMATOR_DURATION_SCALE,
            1f,
        )
    }
}

object LadeMotion {

    object Duration {
        const val Short = 150
        const val Medium = 250
        const val Long = 400
    }

    @Composable
    fun <T> enter(): FiniteAnimationSpec<T> {
        val scale = LocalMotionScale.current
        return if (scale == 0f) snap() else tween((Duration.Medium * scale).toInt())
    }

    @Composable
    fun <T> exit(): FiniteAnimationSpec<T> {
        val scale = LocalMotionScale.current
        return if (scale == 0f) snap() else tween((Duration.Short * scale).toInt())
    }

    @Composable
    fun <T> contentSize(): FiniteAnimationSpec<T> {
        val scale = LocalMotionScale.current
        return if (scale == 0f) {
            snap()
        } else {
            spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMediumLow,
            )
        }
    }
}