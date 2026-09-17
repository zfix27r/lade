package app.lade.calendar.ui.component.week

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLocale
import app.lade.calendar.domain.CalendarDateMode
import app.lade.calendar.domain.CalendarMode
import app.lade.calendar.domain.CalendarStateModel
import app.lade.calendar.ui.component.swipe.CalendarDateSwipe
import java.time.LocalDate
import java.time.temporal.WeekFields

@Composable
fun CalendarWeekLayout(
	state: CalendarStateModel,
	onSwipe: (CalendarDateMode) -> Unit,
	onDateSelected: (LocalDate) -> Unit,
	onEditEntry: (Long) -> Unit,
	modifier: Modifier = Modifier,
) {
	val weekFields = WeekFields.of(LocalLocale.current.platformLocale)
	val weekStart = state.currentDate.with(weekFields.dayOfWeek(), 1L)
	val days = when (state.mode) {
		CalendarMode.DAY -> listOf(state.currentDate)
		CalendarMode.DAY_3 -> (0L..2L).map { state.currentDate.plusDays(it) }
		else -> (0L..6L).map { weekStart.plusDays(it) }
	}
	val entriesByDate = state.entries.groupBy { it.date }
	val today = LocalDate.now()

	Column(modifier = modifier.fillMaxSize()) {
		CalendarWeekStrip(
			mode = state.mode,
			currentDate = state.currentDate,
			entries = state.entries,
			onDateSelected = onDateSelected,
		)
		CalendarDateSwipe(
			onSwipe = onSwipe,
			modifier = Modifier.weight(1f),
		) {
			CalendarWeekTimeline(
				days = days,
				entriesByDate = entriesByDate,
				today = today,
				selectedDate = state.currentDate,
				onOpenDay = onDateSelected,
			)
		}
	}
}