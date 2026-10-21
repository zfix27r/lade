package app.lade.calendar.internal.strip.data

import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import app.lade.calendar.internal.strip.swipe.StripSwipeState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.time.LocalDate

internal class StripStateHolder(initialDate: LocalDate) {

    val stripDate = mutableStateOf(initialDate)
    val widthPx = mutableIntStateOf(0)
    val targetOffsetX = mutableFloatStateOf(0f)
    val targetProgress = mutableFloatStateOf(1f)
    val isDragging = mutableStateOf(false)
    val swipeState = mutableStateOf(StripSwipeState.NONE)

    private val _dateSelected = MutableSharedFlow<LocalDate>(extraBufferCapacity = 8)
    val dateSelected: SharedFlow<LocalDate> = _dateSelected.asSharedFlow()

    fun setStripDate(value: LocalDate) {
        stripDate.value = value
    }

    fun setWidthPx(value: Int) {
        if (widthPx.intValue == value) return
        widthPx.intValue = value
    }

    fun setTargetOffsetX(value: Float) {
        targetOffsetX.floatValue = value
    }

    fun setTargetProgress(value: Float) {
        targetProgress.floatValue = value
    }

    fun setDragging(value: Boolean) {
        if (isDragging.value == value) return
        isDragging.value = value
    }

    fun setSwipeState(value: StripSwipeState) {
        if (swipeState.value == value) return
        swipeState.value = value
    }

    fun commitPageShift() {
        targetOffsetX.floatValue = 0f
    }

    fun followCalendarDate(calendarDate: LocalDate) {
        val current = stripDate.value
        if (calendarDate.year != current.year || calendarDate.month != current.month) {
            stripDate.value = calendarDate
        }
    }

    fun onDateSelected(date: LocalDate) {
        _dateSelected.tryEmit(date)
    }

    fun onSettleComplete() {
        val direction = when {
            targetOffsetX.floatValue > 0f -> -1
            targetOffsetX.floatValue < 0f -> 1
            else -> return
        }
        val isMonthMode = targetProgress.floatValue >= 0.5f
        val newDate = stripShiftDate(
            date = stripDate.value,
            steps = direction,
            isMonthMode = isMonthMode,
        )
        setStripDate(newDate)
        commitPageShift()
    }
}