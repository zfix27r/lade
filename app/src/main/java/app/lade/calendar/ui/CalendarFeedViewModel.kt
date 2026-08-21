package app.lade.calendar.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lade.habits.domain.DueHabitsProjector
import app.lade.habits.domain.HabitHistoryRepository
import app.lade.habits.domain.HabitRepository
import app.lade.habits.domain.MarkHabitDay
import app.lade.habits.domain.model.HabitHistory
import app.lade.habits.domain.model.HabitHistoryResult
import app.lade.habits.domain.model.Habit
import app.lade.habits.ui.mark.HabitMarkUndoEvent
import app.lade.time.domain.ExpandTimeSchedule
import app.lade.time.domain.TimeBlockRepository
import app.lade.time.domain.TimeScheduleRepository
import app.lade.time.domain.model.TimeBlock
import app.lade.time.domain.model.TimeBlockSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class CalendarFeedUiState(
	val entries: List<FeedListEntry> = emptyList(),
	/** Empty set = show all sources. */
	val selectedSources: Set<String> = emptySet(),
	val todayEpochDay: Long = LocalDate.now().toEpochDay(),
)

@HiltViewModel
class CalendarFeedViewModel @Inject constructor(
	scheduleRepository: TimeScheduleRepository,
	blockRepository: TimeBlockRepository,
	historyRepository: HabitHistoryRepository,
	private val habitRepository: HabitRepository,
	private val dueHabitsProjector: DueHabitsProjector,
	private val markHabitDay: MarkHabitDay,
	private val expandTimeSchedule: ExpandTimeSchedule,
) : ViewModel() {
	private val today = LocalDate.now()
	private val from = today.minusDays(FEED_PAST_DAYS)
	private val to = today.plusDays(FEED_FUTURE_DAYS)

	private val selectedSources = MutableStateFlow<Set<String>>(emptySet())

	private val _undoEvents = MutableSharedFlow<HabitMarkUndoEvent>(extraBufferCapacity = 1)
	val undoEvents = _undoEvents.asSharedFlow()

	init {
		viewModelScope.launch {
			scheduleRepository.observeActive().collect { schedules ->
				expandTimeSchedule.syncAll(schedules)
			}
		}
	}

	val state: StateFlow<CalendarFeedUiState> = combine(
		blockRepository.observeBetween(from, to),
		historyRepository.observeBetween(from.toEpochDay(), to.toEpochDay()),
		habitRepository.observeActive(),
		selectedSources,
	) { blocks, histories, habits, sources ->
		val items = buildFeed(blocks, histories, habits)
			.filter { sources.isEmpty() || it.source in sources }
		CalendarFeedUiState(
			entries = groupByDay(items),
			selectedSources = sources,
			todayEpochDay = today.toEpochDay(),
		)
	}.stateIn(
		viewModelScope,
		SharingStarted.WhileSubscribed(5_000),
		CalendarFeedUiState(),
	)

	fun toggleSource(source: String) {
		selectedSources.update { current ->
			if (source in current) current - source else current + source
		}
	}

	fun clearFilters() {
		selectedSources.value = emptySet()
	}

	fun markDone(habitId: Long, dateEpochDay: Long) {
		mark(habitId, dateEpochDay, HabitHistoryResult.DONE)
	}

	fun markSkip(habitId: Long, dateEpochDay: Long) {
		mark(habitId, dateEpochDay, HabitHistoryResult.SKIPPED)
	}

	fun undo(event: HabitMarkUndoEvent) {
		viewModelScope.launch {
			val habit = habitRepository.getById(event.habitId) ?: return@launch
			markHabitDay.restore(habit, event.date, event.previousResult)
		}
	}

	private fun mark(habitId: Long, dateEpochDay: Long, result: String) {
		viewModelScope.launch {
			val habit = habitRepository.getById(habitId) ?: return@launch
			val previous = markHabitDay.mark(
				habit = habit,
				date = LocalDate.ofEpochDay(dateEpochDay),
				result = result,
			)
			_undoEvents.emit(
				HabitMarkUndoEvent(
					habitId = habit.id,
					habitTitle = habit.title,
					dateEpochDay = dateEpochDay,
					newResult = result,
					previousResult = previous,
				),
			)
		}
	}

	private fun buildFeed(
		blocks: List<TimeBlock>,
		histories: List<HabitHistory>,
		habits: List<Habit>,
	): List<FeedItem> {
		val blockItems = blocks.map { block ->
			val startMin = block.start.hour * 60 + block.start.minute
			val endMin = block.end.hour * 60 + block.end.minute
			FeedItem(
				key = "block-${block.id}",
				dateEpochDay = block.date.toEpochDay(),
				title = block.title?.takeIf { it.isNotBlank() }.orEmpty(),
				source = block.source.toFeedSource(),
				blockId = block.id,
				startMinutes = startMin,
				endMinutes = endMin,
				sortMinutes = startMin,
			)
		}
		val historyByHabitDate = histories.associateBy { it.habitId to it.dateEpochDay }
		val habitItems = mutableListOf<FeedItem>()
		for (epoch in from.toEpochDay()..to.toEpochDay()) {
			val date = LocalDate.ofEpochDay(epoch)
			for (habit in dueHabitsProjector.project(date, habits)) {
				val history = historyByHabitDate[habit.id to epoch]
				habitItems += FeedItem(
					key = "habit-${habit.id}-$epoch",
					dateEpochDay = epoch,
					title = history?.title ?: habit.title,
					source = FeedSource.HABIT,
					habitId = habit.id,
					habitResult = history?.result,
					habitGoalValue = history?.goalValue ?: habit.goalValue,
					habitGoalUnit = history?.goalUnit ?: habit.goalUnit,
					habitTimeMinutes = history?.timeOfDayMinutes ?: habit.timeOfDayMinutes,
					sortMinutes = (history?.timeOfDayMinutes ?: habit.timeOfDayMinutes)
						?: (24 * 60 - 1),
				)
			}
		}
		return (blockItems + habitItems).sortedWith(
			compareByDescending<FeedItem> { it.dateEpochDay }
				.thenBy { it.sortMinutes }
				.thenBy { it.key },
		)
	}

	private fun groupByDay(items: List<FeedItem>): List<FeedListEntry> {
		if (items.isEmpty()) return emptyList()
		val out = ArrayList<FeedListEntry>(items.size + 8)
		var lastDay: Long? = null
		for (item in items) {
			if (item.dateEpochDay != lastDay) {
				out += FeedListEntry.DayHeader(item.dateEpochDay)
				lastDay = item.dateEpochDay
			}
			out += FeedListEntry.Row(item)
		}
		return out
	}

	private fun String.toFeedSource(): String = when (this) {
		TimeBlockSource.SCHEDULE -> FeedSource.SCHEDULE
		TimeBlockSource.MANUAL -> FeedSource.MANUAL
		TimeBlockSource.HEALTH -> FeedSource.HEALTH
		TimeBlockSource.CALENDAR -> FeedSource.CALENDAR
		else -> this
	}

	companion object {
		private const val FEED_PAST_DAYS = 60L
		private const val FEED_FUTURE_DAYS = 14L
	}
}
