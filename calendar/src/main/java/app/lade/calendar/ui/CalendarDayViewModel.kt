package app.lade.calendar.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lade.categories.domain.CategoryRepository
import app.lade.entry.data.EntryHistoryResult
import app.lade.entry.domain.EntryDayProjector
import app.lade.entry.domain.EntryHistoryRepository
import app.lade.entry.domain.EntryRepository
import app.lade.entry.domain.KindPriorityRepository
import app.lade.entry.domain.MarkEntryDay
import app.lade.entry.domain.models.EntryMarkUndoEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CalendarDayViewModel @Inject constructor(
	entryRepository: EntryRepository,
	entryHistoryRepository: EntryHistoryRepository,
	kindPriorityRepository: KindPriorityRepository,
	categoryRepository: CategoryRepository,
	private val entryDayProjector: EntryDayProjector,
	private val dayPlanMapper: CalendarDayPlanMapper,
	private val markEntryDay: MarkEntryDay,
) : ViewModel() {
	private val selectedDate = MutableStateFlow(LocalDate.now())

	val date: StateFlow<LocalDate> = selectedDate

	private val _undoEvents = MutableSharedFlow<EntryMarkUndoEvent>(extraBufferCapacity = 1)
	val undoEvents = _undoEvents.asSharedFlow()

	val categoryColors: StateFlow<Map<Long, String>> = categoryRepository.observeActive()
		.map { categories -> categories.associate { it.id to it.color } }
		.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyMap())

	private val dayBundle = selectedDate.flatMapLatest { date ->
		combine(
			entryRepository.observeActive(),
			entryHistoryRepository.observeByDate(date),
			kindPriorityRepository.order,
		) { entries, histories, kindOrder ->
			val plan = entryDayProjector.project(date, entries, histories, kindOrder)
			val byId = entries.associateBy { it.id }
			Triple(plan, dayPlanMapper.toDayBusy(plan), dayPlanMapper.habitSlots(plan).map { slot ->
				val entry = byId[slot.entryId]
				val goal = entry?.goalDefs?.firstOrNull()
				DueHabitItem(
					entryId = slot.entryId,
					title = slot.title,
					categoryId = slot.categoryId,
					goalLabel = goal?.let { g ->
						listOfNotNull(g.target?.toString(), g.unit).joinToString(" ").ifBlank { null }
					},
					timeOfDayMinutes = slot.start?.let { it.hour * 60 + it.minute },
					result = slot.result,
				)
			})
		}
	}

	val dayBusy: StateFlow<CalendarDayBusy> = dayBundle
		.map { it.second }
		.stateIn(
			viewModelScope,
			SharingStarted.WhileSubscribed(5_000),
			CalendarDayBusy(LocalDate.now(), emptyList(), Duration.ZERO, Duration.ofHours(24)),
		)

	val dueHabits: StateFlow<List<DueHabitItem>> = dayBundle
		.map { it.third }
		.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

	fun selectDate(date: LocalDate) {
		selectedDate.value = date
	}

	fun goToday() {
		selectedDate.value = LocalDate.now()
	}

	fun goPreviousDay() {
		selectedDate.value = selectedDate.value.minusDays(1)
	}

	fun goNextDay() {
		selectedDate.value = selectedDate.value.plusDays(1)
	}

	fun markDone(entryId: Long) {
		mark(entryId, EntryHistoryResult.DONE)
	}

	fun markSkip(entryId: Long) {
		mark(entryId, EntryHistoryResult.SKIPPED)
	}

	fun undo(event: EntryMarkUndoEvent) {
		viewModelScope.launch {
			markEntryDay.restore(event.entryId, event.date, event.previousResult)
		}
	}

	private fun mark(entryId: Long, result: String) {
		val date = selectedDate.value
		viewModelScope.launch {
			val previous = markEntryDay.mark(entryId = entryId, date = date, result = result)
			val title = dueHabits.value.find { it.entryId == entryId }?.title.orEmpty()
			_undoEvents.emit(
				EntryMarkUndoEvent(
					entryId = entryId,
					entryTitle = title,
					dateEpochDay = date.toEpochDay(),
					newResult = result,
					previousResult = previous,
				),
			)
		}
	}
}
