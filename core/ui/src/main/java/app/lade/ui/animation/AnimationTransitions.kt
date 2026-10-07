package app.lade.ui.animation

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.ui.unit.IntOffset

fun slideEnter(
    direction: Int,
    slideSpec: FiniteAnimationSpec<IntOffset>,
    fadeSpec: FiniteAnimationSpec<Float>,
): EnterTransition =
    slideInHorizontally(slideSpec, initialOffsetX = { it * direction }) + fadeIn(fadeSpec)

fun slideExit(
    direction: Int,
    slideSpec: FiniteAnimationSpec<IntOffset>,
    fadeSpec: FiniteAnimationSpec<Float>,
): ExitTransition =
    slideOutHorizontally(slideSpec, targetOffsetX = { -it * direction }) + fadeOut(fadeSpec)

fun slideHorizontalTransition(
    forward: Boolean,
    enterSlideSpec: FiniteAnimationSpec<IntOffset>,
    enterFadeSpec: FiniteAnimationSpec<Float>,
    exitSlideSpec: FiniteAnimationSpec<IntOffset>,
    exitFadeSpec: FiniteAnimationSpec<Float>,
): ContentTransform {
    val direction = if (forward) 1 else -1
    return slideEnter(direction, enterSlideSpec, enterFadeSpec) togetherWith
            slideExit(direction, exitSlideSpec, exitFadeSpec)
}