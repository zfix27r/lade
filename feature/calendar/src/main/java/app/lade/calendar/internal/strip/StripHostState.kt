package app.lade.calendar.internal.strip

import androidx.compose.runtime.State
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import app.lade.calendar.internal.strip.data.StripStateHolder
import app.lade.calendar.internal.strip.layout.StripLayout
import app.lade.calendar.internal.strip.swipe.StripSwipeGesture
import app.lade.calendar.internal.strip.swipe.StripSwipeState
import kotlinx.coroutines.flow.SharedFlow
import java.time.LocalDate

internal class StripHostState(
    internal val holder: StripStateHolder,
    internal val gesture: StripSwipeGesture,
    val connection: NestedScrollConnection,
    val swipeState: State<StripSwipeState>,
    val stripDate: State<LocalDate>,
    val dateSelected: SharedFlow<LocalDate>,
    val layout: State<StripLayout>,
    val progress: State<Float>,
)