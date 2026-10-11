package app.lade.calendar.internal.list.strip

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.unit.Dp
import app.lade.calendar.internal.list.strip.data.StripStateHolder
import app.lade.calendar.internal.list.strip.data.stripWeeks
import app.lade.calendar.internal.list.strip.layout.stripLayoutRemember
import app.lade.calendar.internal.list.strip.port.StripListPort
import app.lade.calendar.internal.list.strip.port.StripOutsidePort
import app.lade.calendar.internal.list.strip.port.StripPortImpl
import app.lade.calendar.internal.list.strip.port.StripPortModel
import app.lade.calendar.internal.list.strip.swipe.StripSwipeState
import kotlinx.coroutines.flow.SharedFlow
import java.time.LocalDate
import java.time.temporal.WeekFields

@Composable
internal fun StripHost(
    params: StripHostParams,
): StripHostResult {
    val calendarDateState = remember { mutableStateOf(params.calendarDate) }
    if (calendarDateState.value != params.calendarDate) {
        calendarDateState.value = params.calendarDate
    }
    println("STRIP_LOG StripHost params.calendarDate=${params.calendarDate} state=${calendarDateState.value} markedSize=${params.calendarMarkedDates.value.size}")

    val portModel = remember(params.calendarMarkedDates, calendarDateState) {
        StripPortModel(
            calendarDate = calendarDateState,
            calendarMarkedDates = params.calendarMarkedDates,
        )
    }

    val holder = remember { StripStateHolder(initialDate = params.calendarDate) }

    LaunchedEffect(params.calendarDate) {
        holder.followCalendarDate(params.calendarDate)
    }

    val gesture = rememberStripGestures(holder, params.config)
    val density = LocalDensity.current
    val fullScrollPx = with(density) {
        (params.config.rowHeight * params.config.scrollRows).toPx()
    }
    val locale = LocalLocale.current.platformLocale
    val weekFields = remember(locale) { WeekFields.of(locale) }

    val currentStripDate = holder.stripDate.value
    val centralWeeks = remember(currentStripDate, weekFields) {
        stripWeeks(currentStripDate, weekFields)
    }
    val layout = stripLayoutRemember(
        weeks = centralWeeks,
        activeDate = currentStripDate,
        config = params.config,
    )

    val port = remember(holder, gesture, portModel) {
        StripPortImpl(
            holder = holder,
            gesture = gesture,
            portModel = portModel,
        )
    }

    val currentOnDateSelected by rememberUpdatedState(params.onDateSelected)
    LaunchedEffect(port) {
        port.dateSelected.collect { currentOnDateSelected(it) }
    }

    StripSwipeRow(
        viewPort = port,
        gesturePort = port,
        config = params.config,
        modifier = params.modifier,
    )

    val connection = remember(port, params.listState, fullScrollPx) {
        StripConnection(port, params.listState, fullScrollPx)
    }

    val listPort = remember(port, connection) {
        object : StripListPort {
            override val connection: NestedScrollConnection = connection
        }
    }

    val outsidePort = remember(port) {
        object : StripOutsidePort {
            override val swipeState: State<StripSwipeState> = port.swipeState
            override val stripDate: State<LocalDate> = port.stripDate
            override val dateSelected: SharedFlow<LocalDate> = port.dateSelected
        }
    }

    return remember(listPort, outsidePort, layout, holder) {
        StripHostResult(
            listPort = listPort,
            outsidePort = outsidePort,
            topOffset = { layout.listTopOffsetY(holder.targetProgress.floatValue) },
        )
    }
}