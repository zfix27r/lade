package app.lade.notifications

import app.lade.entry.domain.EntryDayProjector
import app.lade.entry.domain.EntryRepository
import app.lade.entry.domain.models.Entry
import app.lade.entry.domain.models.EntryKind
import app.lade.notifications.data.ReminderAlarmScheduler
import app.lade.notifications.data.ReminderChannels
import app.lade.notifications.domain.ReminderFireMode
import app.lade.notifications.domain.ReminderKind
import app.lade.notifications.domain.ReminderRequest
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Rescheduler @Inject constructor(
    private val entryRepository: EntryRepository,
    private val entryDayProjector: EntryDayProjector,
    private val scheduler: ReminderAlarmScheduler,
    private val channels: ReminderChannels,
) {
    suspend fun rescheduleAll() {
        channels.ensureCreated()
        val zone = ZoneId.systemDefault()
        val today = LocalDate.now(zone)
        val entries = entryRepository.observeActive().first()
        for (offset in 0 until HORIZON_DAYS) {
            val date = today.plusDays(offset.toLong())
            val plan = entryDayProjector.project(date, entries, emptyList())
            val byId = entries.associateBy { it.id }

            for (slot in plan.untimed + plan.timed) {
                val entry = byId[slot.entryId] ?: continue
                when (entry.kind) {
                    EntryKind.HABIT -> scheduleHabit(entry, date, slot.start, zone)
                    EntryKind.SCHEDULE, EntryKind.EVENT -> {
                        val start = slot.start ?: continue
                        scheduleTimed(entry, date, start, zone, ReminderKind.BLOCK)
                    }
                    EntryKind.TASK -> {
                        val start = slot.start ?: continue
                        val mode = ReminderFireMode.fromAlarmStorage(entry.alarmMode)
                        if (mode != null) {
                            scheduleTimed(entry, date, start, zone, ReminderKind.HABIT, mode)
                        }
                    }
                }
            }
        }
    }

    private fun scheduleHabit(
        entry: Entry,
        date: LocalDate,
        slotStart: LocalTime?,
        zone: ZoneId,
    ) {
        val mode = ReminderFireMode.fromAlarmStorage(entry.alarmMode) ?: return
        val timeOfDay = slotStart ?: entry.startTime ?: return
        val ahead = entry.reminderMinutesBefore ?: 0
        var trigger = LocalDateTime.of(date, timeOfDay).minusMinutes(ahead.toLong())
        if (trigger.toLocalDate().isBefore(date) && ahead > 0) {
            trigger = LocalDateTime.of(date, timeOfDay)
        }
        val epochMs = trigger.atZone(zone).toInstant().toEpochMilli()
        scheduler.schedule(
            ReminderRequest(
                kind = ReminderKind.HABIT,
                entityId = entry.id,
                epochDay = date.toEpochDay(),
                triggerAtEpochMs = epochMs,
                title = entry.title,
                mode = mode,
            ),
        )
    }

    private fun scheduleTimed(
        entry: Entry,
        date: LocalDate,
        start: LocalTime,
        zone: ZoneId,
        kind: ReminderKind,
        mode: ReminderFireMode = ReminderFireMode.NOTIFICATION,
    ) {
        val trigger = LocalDateTime.of(date, start)
        val epochMs = trigger.atZone(zone).toInstant().toEpochMilli()
        scheduler.schedule(
            ReminderRequest(
                kind = kind,
                entityId = entry.id,
                epochDay = date.toEpochDay(),
                triggerAtEpochMs = epochMs,
                title = entry.title.ifBlank { start.toString() },
                mode = mode,
            ),
        )
    }

    companion object {
        private const val HORIZON_DAYS = 14
    }
}