package app.lade.calendar.internal.strip

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.unit.dp
import app.lade.calendar.api.config.StripConfig
import app.lade.calendar.internal.main.CalendarModeActions
import app.lade.calendar.internal.strip.data.stripWeeks
import app.lade.calendar.internal.strip.swipe.rememberStripSwipeAnimations
import app.lade.calendar.internal.strip.swipe.stripGesture
import app.lade.calendardata.api.models.CalendarDataModel
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import java.time.temporal.WeekFields

@Composable
internal fun CalendarStripView(
    host: StripHostState,
    currentDate: StateFlow<LocalDate>,
    days: StateFlow<List<CalendarDataModel>>,
    marks: StateFlow<Map<LocalDate, CalendarDataMinModel>>,
    actions: CalendarModeActions,
    onDateSelected: (LocalDate) -> Unit,
    config: StripConfig,
    modifier: Modifier = Modifier,
) {
    val holder = host.holder
    val gesture = host.gesture

    val density = LocalDensity.current
    val locale = LocalLocale.current.platformLocale
    val weekFields = remember(locale) { WeekFields.of(locale) }
    val fullScrollPx = with(density) {
        (config.rowHeight * config.scrollRows).toPx()
    }
    val thresholdPx = with(density) { 8.dp.toPx() }

    val currentOnDateSelected by rememberUpdatedState(onDateSelected)
    LaunchedEffect(holder) {
        holder.dateSelected.collect { currentOnDateSelected(it) }
    }

    val stripDate by host.stripDate
    val layout by host.layout

    val (offsetXState, progressState) = rememberStripSwipeAnimations(
        holder = holder,
        lastVelocityX = { gesture.lastVelocityX },
        onSettleComplete = { holder.onSettleComplete() },
    )

    val onTap = rememberTapHandler(
        weeks = stripWeeks(stripDate, weekFields),
        layout = layout,
        progressState = progressState,
        offsetXState = offsetXState,
        widthPxProvider = { holder.widthPx.intValue },
        density = density,
        onDateSelected = holder::onDateSelected,
    )

    var measuredWidthPx by remember { mutableIntStateOf(0) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(layout.monthHeight)
            .background(MaterialTheme.colorScheme.surface)
            .clipToBounds()
            .onSizeChanged {
                measuredWidthPx = it.width
                holder.setWidthPx(it.width)
            }
            .stripGesture(
                gesture = gesture,
                thresholdPx = thresholdPx,
                fullScrollPx = fullScrollPx,
                stripDate = stripDate,
                onTap = onTap,
            ),
    ) {
        StripPages(
            stripDate = stripDate,
            calendarDate = currentDate,
            markedDates = marks,
            progressState = progressState,
            offsetXState = offsetXState,
            widthPx = measuredWidthPx,
            weekFields = weekFields,
            config = config,
        )
    }
}