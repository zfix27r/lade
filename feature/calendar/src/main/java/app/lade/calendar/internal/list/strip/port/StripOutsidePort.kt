package app.lade.calendar.internal.list.strip.port

import androidx.compose.runtime.State
import app.lade.calendar.internal.list.strip.swipe.StripSwipeState
import kotlinx.coroutines.flow.SharedFlow
import java.time.LocalDate

internal interface StripOutsidePort {

    val swipeState: State<StripSwipeState>
    val stripDate: State<LocalDate>
    val dateSelected: SharedFlow<LocalDate>
}