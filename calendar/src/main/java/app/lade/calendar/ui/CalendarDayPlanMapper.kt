package app.lade.calendar.ui

import app.lade.entry.domain.DayPlan
import app.lade.entry.domain.DaySlot
import app.lade.entry.domain.models.EntryKind
import java.time.Duration
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalendarDayPlanMapper @Inject constructor() {
	fun toDayBusy(plan: DayPlan): CalendarDayBusy {
		val intervals = plan.timed
			.filter { it.kind == EntryKind.SCHEDULE || it.kind == EntryKind.EVENT }
			.map { slot ->
				CalendarBusyInterval(
					blockId = slot.entryId,
					entryId = slot.entryId,
					title = slot.title,
					categoryId = slot.categoryId,
					start = slot.start!!,
					end = slot.end!!,
					source = when {
						slot.kind == EntryKind.SCHEDULE || slot.fromSeries -> CalendarBusySource.SCHEDULE
						else -> CalendarBusySource.MANUAL
					},
				)
			}
		val busy = intervals.fold(Duration.ZERO) { acc, item -> acc + item.duration }
		val free = (Duration.ofHours(24) - busy).coerceAtLeast(Duration.ZERO)
		return CalendarDayBusy(
			date = plan.date,
			intervals = intervals,
			busy = busy,
			free = free,
		)
	}

	fun habitItems(plan: DayPlan): List<DueHabitItem> =
		habitSlots(plan).map { slot ->
			DueHabitItem(
				entryId = slot.entryId,
				title = slot.title,
				categoryId = slot.categoryId,
				goalLabel = null,
				timeOfDayMinutes = slot.start?.let { it.hour * 60 + it.minute },
				result = slot.result,
			)
		}

	fun habitSlots(plan: DayPlan): List<DaySlot> =
		plan.untimed.filter { it.kind == EntryKind.HABIT } +
			plan.timed.filter { it.kind == EntryKind.HABIT }

	fun hasBusy(plan: DayPlan): Boolean =
		plan.timed.any { it.kind == EntryKind.SCHEDULE || it.kind == EntryKind.EVENT }

	fun hasHabits(plan: DayPlan): Boolean =
		habitSlots(plan).isNotEmpty()
}
