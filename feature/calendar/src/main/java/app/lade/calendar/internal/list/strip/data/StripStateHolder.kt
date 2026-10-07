package app.lade.calendar.internal.list.strip.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate

internal class StripStateHolder(initialDate: LocalDate = LocalDate.now()) {

    private val _state = MutableStateFlow(StripState(date = initialDate))
    val state: StateFlow<StripState> = _state.asStateFlow()

    fun setDate(date: LocalDate) {
        _state.update { it.copy(date = date) }
    }

    fun setOffsetX(offsetX: Float) {
        _state.update { it.copy(offsetX = offsetX) }
    }

    fun setProgress(progress: Float) {
        _state.update { it.copy(progress = progress) }
    }

    fun setWidthPx(widthPx: Int) {
        if (_state.value.widthPx == widthPx) return
        _state.update { it.copy(widthPx = widthPx) }
    }

    fun setAnchor(date: LocalDate) {
        _state.update { it.copy(date = date, offsetX = 0f) }
    }
}