package app.lade.agenda.data.day

import app.lade.agenda.domain.models.DayPlan
import app.lade.agenda.domain.models.DaySlot
import app.lade.agenda.api.entry.EntryKind
import app.lade.agenda.api.entry.EntryModel
import app.lade.agenda.api.log.LogModel
import app.lade.temporal.domain.ScheduleEngine
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DayProject @Inject constructor(
    private val scheduleEngine: ScheduleEngine,
) {
	fun project(
        date: LocalDate,
        entries: List<EntryModel>,
        logs: List<LogModel>,
        kindOrder: List<EntryKind> = EntryKind.entries,
	): DayPlan {
		val epochDay = date.toEpochDay()
		val logsForDate = logs.filter { it.epochDay == epochDay }
		val logsByEntry: Map<Long, List<LogModel>> = logsForDate.groupBy { it.entryId }

		val active = entries.filter { !it.isArchived && !it.isPaused }
		val projected = ArrayList<DaySlot>()

		for (entry in active) {
			val entryLogs = logsByEntry[entry.id].orEmpty()
			when (entry.kind) {
				EntryKind.SCHEDULE -> projectSchedule(date, entry)?.let { projected += it }
				EntryKind.HABIT -> projectHabit(date, entry, entryLogs)?.let { projected += it }
				EntryKind.TASK -> projectTask(date, entry, entryLogs)?.let { projected += it }
				EntryKind.EVENT -> projectEvent(date, entry, entryLogs)?.let { projected += it }
				else -> {}
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
				compareBy<DaySlot> { kindOrder.indexOf(it.kind) }
					.thenBy { it.title },
			)
		val timed = masked
			.filter { it.hasTime }
			.sortedWith(
				compareBy<DaySlot> { it.start }
					.thenByDescending { kindOrder.indexOf(it.kind) }
					.thenByDescending { it.duration }
					.thenBy { it.title },
			)
		return DayPlan(date = date, untimed = untimed, timed = timed)
	}

	fun projectBetween(
        fromInclusive: LocalDate,
        toInclusive: LocalDate,
        entries: List<EntryModel>,
        logs: List<LogModel>,
        kindOrder: List<EntryKind> = EntryKind.entries,
	): Map<LocalDate, DayPlan> {
		val fromEpoch = fromInclusive.toEpochDay()
		val toEpoch = toInclusive.toEpochDay()
		val logsInRange = logs.filter { it.epochDay in fromEpoch..toEpoch }
		val result = LinkedHashMap<LocalDate, DayPlan>()
		var d = fromInclusive
		while (!d.isAfter(toInclusive)) {
			result[d] = project(d, entries, logsInRange, kindOrder)
			d = d.plusDays(1)
		}
		return result
	}

	private fun projectSchedule(date: LocalDate, entryModel: EntryModel): DaySlot? {
		val start = entryModel.startTime ?: return null
		val end = entryModel.endTime ?: return null
		if (end <= start) return null
		val from = entryModel.dateFrom ?: return null
		if (date.isBefore(from)) return null
		entryModel.dateTo?.let { if (date.isAfter(it)) return null }
		val rrule = entryModel.rrule ?: return null
		if (!scheduleEngine.isDue(rrule, date, dtStart = from)) return null
		return DaySlot(
            entry = entryModel,
            logs = emptyList(),
            fromSeries = true,
        )
	}

	private fun projectHabit(
        date: LocalDate,
        entryModel: EntryModel,
        logs: List<LogModel>,
	): DaySlot? {
		val rrule = entryModel.rrule ?: return null
		val anchor = entryModel.dateFrom ?: RRULE_ANCHOR
		if (!scheduleEngine.isDue(rrule, date, dtStart = anchor)) return null
		return DaySlot(
            entry = entryModel,
            logs = logs,
            fromSeries = entryModel.isSeries,
        )
	}

	private fun projectTask(
        date: LocalDate,
        entryModel: EntryModel,
        logs: List<LogModel>,
	): DaySlot? {
		val due = entryModel.dateFrom ?: return null
		if (date.isBefore(due)) return null
		entryModel.dateTo?.let { if (date.isAfter(it)) return null }
		val rrule = entryModel.rrule
		if (!rrule.isNullOrBlank() && !scheduleEngine.isDue(rrule, date, dtStart = due)) {
			return null
		}
		return DaySlot(
            entry = entryModel,
            logs = logs,
            fromSeries = false,
        )
	}

	private fun projectEvent(
        date: LocalDate,
        entryModel: EntryModel,
        logs: List<LogModel>,
	): DaySlot? {
		val day = entryModel.dateFrom ?: return null
		if (entryModel.isSeries) {
			val rrule = entryModel.rrule ?: return null
			if (date.isBefore(day)) return null
			entryModel.dateTo?.let { if (date.isAfter(it)) return null }
			if (!scheduleEngine.isDue(rrule, date, dtStart = day)) return null
		} else if (date != day) {
			return null
		}
		return DaySlot(
            entry = entryModel,
            logs = logs,
            fromSeries = entryModel.isSeries,
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