package app.lade.calendar.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lade.calendar.domain.FeedItem
import app.lade.calendar.domain.FeedListEntry
import app.lade.calendar.domain.FeedSource
import app.lade.entry.data.EntryHistoryResult
import app.lade.entry.domain.EntryDayProjector
import app.lade.entry.domain.EntryHistoryRepository
import app.lade.entry.domain.EntryRepository
import app.lade.entry.domain.KindPriorityRepository
import app.lade.entry.domain.MarkEntryDay
import app.lade.entry.domain.models.EntryKind
import app.lade.entry.domain.models.EntryMarkUndoEvent
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
	val selectedSources: Set<String> = emptySet(),
	val todayEpochDay: Long = LocalDate.now().toEpochDay(),
)

@HiltViewModel
class CalendarFeedViewModel @Inject constructor(
	entryRepository: EntryRepository,
	entryHistoryRepository: EntryHistoryRepository,
	kindPriorityRepository: KindPriorityRepository,
	private val entryDayProjector: EntryDayProjector,
	private val dayPlanMapper: CalendarDayPlanMapper,
	private val markEntryDay: MarkEntryDay,
) : ViewModel() {
	private val today = LocalDate.now()
	private val from = today.minusDays(FEED_PAST_DAYS)
	private val to = today.plusDays(FEED_FUTURE_DAYS)

	private val selectedSources = MutableStateFlow<Set<String>>(emptySet())

	private val _undoEvents = MutableSharedFlow<EntryMarkUndoEvent>(extraBufferCapacity = 1)
	val undoEvents = _undoEvents.asSharedFlow()

	val state: StateFlow<CalendarFeedUiState> = combine(
		entryRepository.observeActive(),
		entryHistoryRepository.observeBetween(from, to),
		kindPriorityRepository.order,
		selectedSources,
	) { entries, histories, kindOrder, sources ->
		val plans = entryDayProjector.projectBetween(from, to, entries, histories, kindOrder)
		val items = buildFeed(plans)
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

	fun markDone(entryId: Long, dateEpochDay: Long) {
		mark(entryId, dateEpochDay, EntryHistoryResult.DONE)
	}

	fun markSkip(entryId: Long, dateEpochDay: Long) {
		mark(entryId, dateEpochDay, EntryHistoryResult.SKIPPED)
	}

	fun undo(event: EntryMarkUndoEvent) {
		viewModelScope.launch {
			markEntryDay.restore(event.entryId, event.date, event.previousResult)
		}
	}

	private fun mark(entryId: Long, dateEpochDay: Long, result: String) {
		viewModelScope.launch {
			val previous = markEntryDay.mark(
				entryId = entryId,
				date = LocalDate.ofEpochDay(dateEpochDay),
				result = result,
			)
			_undoEvents.emit(
				EntryMarkUndoEvent(
					entryId = entryId,
					entryTitle = "",
					dateEpochDay = dateEpochDay,
					newResult = result,
					previousResult = previous,
				),
			)
		}
	}

	private fun buildFeed(plans: Map<LocalDate, app.lade.entry.domain.DayPlan>): List<FeedItem> {
		val items = mutableListOf<FeedItem>()
		for ((date, plan) in plans) {
			val epoch = date.toEpochDay()
			for (slot in plan.timed) {
				if (slot.kind != EntryKind.SCHEDULE && slot.kind != EntryKind.EVENT) continue
				val startMin = slot.start!!.hour * 60 + slot.start!!.minute
				val endMin = slot.end!!.hour * 60 + slot.end!!.minute
				val source = when {
					slot.kind == EntryKind.SCHEDULE || slot.fromSeries -> FeedSource.SCHEDULE
					else -> FeedSource.MANUAL
				}
				items += FeedItem(
					key = "entry-${slot.entryId}-$epoch",
					dateEpochDay = epoch,
					title = slot.title,
					source = source,
					blockId = slot.entryId,
					entryId = slot.entryId,
					startMinutes = startMin,
					endMinutes = endMin,
					sortMinutes = startMin,
				)
			}
			for (slot in dayPlanMapper.habitSlots(plan)) {
				val timeMin = slot.start?.let { it.hour * 60 + it.minute }
				items += FeedItem(
					key = "habit-${slot.entryId}-$epoch",
					dateEpochDay = epoch,
					title = slot.title,
					source = FeedSource.HABIT,
					entryId = slot.entryId,
					habitId = slot.entryId,
					habitResult = slot.result,
					habitTimeMinutes = timeMin,
					sortMinutes = timeMin ?: (24 * 60 - 1),
				)
			}
		}
		return items.sortedWith(
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

	companion object {
		private const val FEED_PAST_DAYS = 60L
		private const val FEED_FUTURE_DAYS = 14L
	}
}
