package app.lade.calendar.ui

import CalendarBusySource
import app.lade.agenda.api.agenda.AgendaModel
import app.lade.agenda.api.entry.EntryKind
import app.lade.calendar.domain.CalendarBusyInterval
import app.lade.calendar.domain.CalendarDayBusy
import app.lade.calendar.domain.DueHabitItem
import java.time.Duration
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalendarDayPlanMapper @Inject constructor() {
	fun toDayBusy(date: LocalDate, agendas: List<AgendaModel>): CalendarDayBusy {
		val intervals = agendas
			.filter { it.entry.kind == EntryKind.SCHEDULE || it.entry.kind == EntryKind.EVENT }
			.mapNotNull { agenda ->
				val entry = agenda.entry
				val start = entry.startTime ?: return@mapNotNull null
				val end = entry.endTime ?: return@mapNotNull null
				if (end <= start) return@mapNotNull null
				CalendarBusyInterval(
					blockId = entry.id,
					entryId = entry.id,
					title = entry.title,
					start = start,
					end = end,
					source = when {
						entry.kind == EntryKind.SCHEDULE || agenda.fromSeries ->
							CalendarBusySource.SCHEDULE
						else -> CalendarBusySource.MANUAL
					},
				)
			}
		val busy = intervals.fold(Duration.ZERO) { acc, item -> acc + item.duration }
		val free = (Duration.ofHours(24) - busy).coerceAtLeast(Duration.ZERO)
		return CalendarDayBusy(
			date = date,
			intervals = intervals,
			busy = busy,
			free = free,
		)
	}

	fun habitItems(agendas: List<AgendaModel>): List<DueHabitItem> =
		agendas.filter { it.entry.kind == EntryKind.HABIT }.map { agenda ->
			val goal = agenda.goals.firstOrNull()
			DueHabitItem(
				entryId = agenda.entry.id,
				title = agenda.entry.title,
				goalLabel = goal?.let { g ->
					listOfNotNull(g.amount?.toString(), g.unit).joinToString(" ")
						.ifBlank { null }
				},
				timeOfDayMinutes = agenda.entry.startTime?.let { it.hour * 60 + it.minute },
				done = agenda.isDone(),
			)
		}

	private fun AgendaModel.isDone(): Boolean =
		logs.any { (it.actualAmount ?: 0) > 0 }
}
