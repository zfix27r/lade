package app.lade.entry.domain

import app.lade.entry.data.EntryHistoryResult
import app.lade.entry.domain.models.Entry
import app.lade.entry.domain.models.EntryHistory
import app.lade.entry.domain.models.EntryKind
import app.lade.temporal.domain.ScheduleEngine
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class EntryDayProjector @Inject constructor(
	private val scheduleEngine: ScheduleEngine,
) {
	fun project(
		date: LocalDate,
		entries: List<Entry>,
		histories: List<EntryHistory>,
		kindOrder: List<EntryKind> = KindPriority.DEFAULT,
	): DayPlan {
		val historyForDate = histories.filter { it.date == date }
		val historyByEntry = historyForDate.filter { it.entryId != null }.associateBy { it.entryId!! }

		val active = entries.filter { !it.isArchived && !it.isPaused }
		val projected = ArrayList<DaySlot>()

		for (entry in active) {
			when (entry.kind) {
				EntryKind.SCHEDULE -> projectSchedule(date, entry)?.let { projected += it }
				EntryKind.HABIT -> projectHabit(date, entry, historyByEntry[entry.id])?.let { projected += it }
				EntryKind.TASK -> projectTask(date, entry, historyByEntry[entry.id])?.let { projected += it }
				EntryKind.EVENT -> projectEvent(date, entry, historyByEntry[entry.id])?.let { projected += it }
			}
		}

		val singles = projected.filter { !it.fromSeries && it.hasTime }
		val masked = projected.filter { slot ->
			if (!slot.fromSeries || !slot.hasTime) return@filter true
			singles.none { single -> overlaps(single, slot) }
		}

		val untimed = masked
			.filter { !it.hasTime }
			.sortedWith(
				compareBy<DaySlot> { KindPriority.rank(it.kind, kindOrder) }
					.thenBy { it.title },
			)
		val timed = masked
			.filter { it.hasTime }
			.sortedWith(
				compareBy<DaySlot> { it.start }
					.thenByDescending { KindPriority.rank(it.kind, kindOrder) }
					.thenByDescending { it.duration }
					.thenBy { it.title },
			)
		return DayPlan(date = date, untimed = untimed, timed = timed)
	}

	fun projectBetween(
		fromInclusive: LocalDate,
		toInclusive: LocalDate,
		entries: List<Entry>,
		histories: List<EntryHistory>,
		kindOrder: List<EntryKind> = KindPriority.DEFAULT,
	): Map<LocalDate, DayPlan> {
		val result = LinkedHashMap<LocalDate, DayPlan>()
		var d = fromInclusive
		while (!d.isAfter(toInclusive)) {
			result[d] = project(d, entries, histories.filter {
				!it.date.isBefore(fromInclusive) && !it.date.isAfter(toInclusive)
			}, kindOrder)
			d = d.plusDays(1)
		}
		return result
	}

	private fun projectSchedule(date: LocalDate, entry: Entry): DaySlot? {
		val start = entry.startTime ?: return null
		val end = entry.endTime ?: return null
		if (end <= start) return null
		val from = entry.dateFrom ?: return null
		if (date.isBefore(from)) return null
		entry.dateTo?.let { if (date.isAfter(it)) return null }
		val rrule = entry.rrule ?: return null
		if (!scheduleEngine.isDue(rrule, date, dtStart = from)) return null
		return DaySlot(
			entryId = entry.id,
			kind = EntryKind.SCHEDULE,
			title = entry.title,
			categoryId = entry.categoryId,
			start = start,
			end = end,
			fromSeries = true,
		)
	}

	private fun projectHabit(date: LocalDate, entry: Entry, history: EntryHistory?): DaySlot? {
		val rrule = entry.rrule ?: return null
		val anchor = entry.dateFrom ?: RRULE_ANCHOR
		if (!scheduleEngine.isDue(rrule, date, dtStart = anchor)) return null
		val start = entry.startTime
		val end = entry.endTime ?: start?.plusMinutes(30)
		return DaySlot(
			entryId = entry.id,
			historyId = history?.id,
			kind = EntryKind.HABIT,
			title = entry.title,
			categoryId = entry.categoryId,
			start = start,
			end = if (start != null && end != null && end > start) end else null,
			result = history?.result,
			fromSeries = entry.isSeries,
		)
	}

	private fun projectTask(date: LocalDate, entry: Entry, history: EntryHistory?): DaySlot? {
		if (history?.result == EntryHistoryResult.DONE ||
			history?.result == EntryHistoryResult.CANCELLED
		) {
			return null
		}
		val due = entry.dateFrom ?: return null
		if (date.isBefore(due)) return null
		entry.dateTo?.let { if (date.isAfter(it)) return null }
		val rrule = entry.rrule
		if (!rrule.isNullOrBlank() && !scheduleEngine.isDue(rrule, date, dtStart = due)) {
			return null
		}
		val start = entry.startTime
		val end = entry.endTime
		return DaySlot(
			entryId = entry.id,
			historyId = history?.id,
			kind = EntryKind.TASK,
			title = entry.title,
			categoryId = entry.categoryId,
			start = start,
			end = if (start != null && end != null && end > start) end else null,
			result = history?.result,
			fromSeries = false,
		)
	}

	private fun projectEvent(date: LocalDate, entry: Entry, history: EntryHistory?): DaySlot? {
		val day = entry.dateFrom ?: return null
		if (entry.isSeries) {
			val rrule = entry.rrule ?: return null
			if (date.isBefore(day)) return null
			entry.dateTo?.let { if (date.isAfter(it)) return null }
			if (!scheduleEngine.isDue(rrule, date, dtStart = day)) return null
		} else if (date != day) {
			return null
		}
		val start = entry.startTime
		val end = entry.endTime
		return DaySlot(
			entryId = entry.id,
			historyId = history?.id,
			kind = EntryKind.EVENT,
			title = entry.title,
			categoryId = entry.categoryId,
			start = start,
			end = if (start != null && end != null && end > start) end else null,
			result = history?.result,
			fromSeries = entry.isSeries,
		)
	}

	private fun overlaps(a: DaySlot, b: DaySlot): Boolean {
		val a0 = a.start ?: return false
		val a1 = a.end ?: return false
		val b0 = b.start ?: return false
		val b1 = b.end ?: return false
		return a0 < b1 && b0 < a1
	}

	companion object {
		val RRULE_ANCHOR: LocalDate = LocalDate.of(1970, 1, 5)
	}
}
