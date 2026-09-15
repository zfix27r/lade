package app.lade.calendar.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lade.agenda.api.AgendaApi
import app.lade.agenda.api.agenda.AgendaModel
import app.lade.agenda.api.entry.EntryKind
import app.lade.agenda.api.log.LogOrigin
import app.lade.agenda.api.log.LogSaveGoalModel
import app.lade.agenda.api.log.LogSaveModel
import app.lade.calendar.domain.FeedItem
import app.lade.calendar.domain.FeedListEntry
import app.lade.calendar.domain.FeedSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class CalendarFeedUiState(
	val entries: List<FeedListEntry> = emptyList(),
	val selectedSources: Set<FeedSource> = emptySet(),
	val todayEpochDay: Long = LocalDate.now().toEpochDay(),
)

@HiltViewModel
class CalendarFeedViewModel @Inject constructor(
	private val agendaApi: AgendaApi,
) : ViewModel() {
	private val today = LocalDate.now()
	private val from = today.minusDays(FEED_PAST_DAYS)
	private val to = today.plusDays(FEED_FUTURE_DAYS)

	private val selectedSources = MutableStateFlow<Set<FeedSource>>(emptySet())

	val state: StateFlow<CalendarFeedUiState> = combine(
		agendaApi.observeRange(from, to),
		selectedSources,
	) { agendas, sources ->
		val items = buildFeed(agendas)
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

	fun toggleSource(source: FeedSource) {
		selectedSources.update { current ->
			if (source in current) current - source else current + source
		}
	}

	fun clearFilters() {
		selectedSources.value = emptySet()
	}

	fun markDone(entryId: Long, dateEpochDay: Long) = mark(entryId, dateEpochDay, done = true)

	fun markSkip(entryId: Long, dateEpochDay: Long) = mark(entryId, dateEpochDay, done = false)

	private fun mark(entryId: Long, dateEpochDay: Long, done: Boolean) {
		viewModelScope.launch {
			val date = LocalDate.ofEpochDay(dateEpochDay)
			val agenda = agendaApi.observeRange(date, date)
				.first()
				.find { it.entry.id == entryId }
				?: return@launch
			val goals = agenda.goals.map { goal ->
				LogSaveGoalModel(
					goalId = goal.id,
					amount = if (done) goal.amount else 0,
					repeat = if (done) goal.repeat else 0,
					weight = goal.weight,
				)
			}
			val saveModel = LogSaveModel(
				date = date,
				goals = goals,
				origin = LogOrigin.CALENDAR,
			)
			agendaApi.saveLogs(saveModel)
		}
	}

	private fun buildFeed(agendas: List<AgendaModel>): List<FeedItem> {
		val items = mutableListOf<FeedItem>()
		for (agenda in agendas) {
			val epoch = agenda.date.toEpochDay()
			val entry = agenda.entry
			when (entry.kind) {
				EntryKind.SCHEDULE, EntryKind.EVENT -> {
					val start = entry.startTime ?: continue
					val end = entry.endTime ?: continue
					val startMin = start.hour * 60 + start.minute
					val endMin = end.hour * 60 + end.minute
					val source = if (entry.kind == EntryKind.SCHEDULE || agenda.fromSeries) {
						FeedSource.SCHEDULE
					} else {
						FeedSource.MANUAL
					}
					items += FeedItem(
						key = "entry-${entry.id}-$epoch",
						dateEpochDay = epoch,
						title = entry.title,
						source = source,
						blockId = entry.id,
						entryId = entry.id,
						startMinutes = startMin,
						endMinutes = endMin,
						sortMinutes = startMin,
					)
				}
				EntryKind.HABIT -> {
					val timeMin = entry.startTime?.let { it.hour * 60 + it.minute }
					items += FeedItem(
						key = "habit-${entry.id}-$epoch",
						dateEpochDay = epoch,
						title = entry.title,
						source = FeedSource.HABIT,
						entryId = entry.id,
						habitId = entry.id,
						habitDone = agenda.isDone(),
						habitTimeMinutes = timeMin,
						sortMinutes = timeMin ?: (24 * 60 - 1),
					)
				}
				else -> Unit
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

	private fun AgendaModel.isDone(): Boolean =
		logs.any { (it.actualAmount ?: 0) > 0 }

	companion object {
		private const val FEED_PAST_DAYS = 60L
		private const val FEED_FUTURE_DAYS = 14L
	}
}