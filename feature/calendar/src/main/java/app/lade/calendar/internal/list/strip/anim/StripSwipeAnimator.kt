package app.lade.calendar.internal.list.strip.anim

import androidx.compose.animation.core.Animatable
import app.lade.calendar.internal.list.strip.data.StripStateHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

internal class StripSwipeAnimator(
    private val holder: StripStateHolder,
) {

    private var scope: CoroutineScope? = null
    private var offsetJob: Job? = null
    private var progressJob: Job? = null

    fun attach(scope: CoroutineScope) {
        this.scope = scope
    }

    fun animateOffsetTo(target: Float, onFinish: (() -> Unit)? = null) {
        offsetJob?.cancel()
        val s = scope ?: return
        val start = holder.state.value.offsetX
        offsetJob = s.launch {
            Animatable(start).animateTo(
                targetValue = target,
                animationSpec = StripSettleSpec.offset(),
            ) {
                holder.setOffsetX(value)
            }
            onFinish?.invoke()
        }
    }

    fun animateProgressTo(target: Float) {
        progressJob?.cancel()
        val s = scope ?: return
        val start = holder.state.value.progress
        progressJob = s.launch {
            Animatable(start).animateTo(
                targetValue = target,
                animationSpec = StripSettleSpec.progress(),
            ) {
                holder.setProgress(value)
            }
        }
    }

    fun cancelAll() {
        offsetJob?.cancel()
        progressJob?.cancel()
    }
}