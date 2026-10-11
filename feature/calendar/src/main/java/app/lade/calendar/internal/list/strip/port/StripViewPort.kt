package app.lade.calendar.internal.list.strip.port

import androidx.compose.runtime.State
import java.time.LocalDate

internal interface StripViewPort {

    val portModel: StripPortModel
    val stripDate: State<LocalDate>
    val widthPx: State<Int>
    val targetOffsetX: State<Float>
    val targetProgress: State<Float>
    val isDragging: State<Boolean>

    fun onWidthChanged(widthPx: Int)
    fun onDateSelected(date: LocalDate)
    fun onSettleComplete()
}