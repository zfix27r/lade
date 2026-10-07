package app.lade.calendar.internal.list.strip.anim

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring

internal object StripSettleSpec {

    fun offset(): FiniteAnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMediumLow,
    )

    fun progress(): FiniteAnimationSpec<Float> = offset()
}