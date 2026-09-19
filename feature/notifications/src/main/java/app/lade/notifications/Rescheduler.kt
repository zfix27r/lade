package app.lade.notifications

import app.lade.agenda.api.AgendaApi
import app.lade.agenda.api.agenda.AgendaModel
import app.lade.entrykind.EntryKind
import app.lade.notifications.data.ReminderAlarmScheduler
import app.lade.notifications.data.ReminderChannels
import app.lade.notifications.domain.ReminderFireMode
import app.lade.notifications.domain.ReminderKind
import app.lade.notifications.domain.ReminderRequest
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Rescheduler @Inject constructor(
    private val agendaApi: AgendaApi,
    private val scheduler: ReminderAlarmScheduler,
    private val channels: ReminderChannels,
) {
    suspend fun rescheduleAll() {
        channels.ensureCreated()
        val zone = ZoneId.systemDefault()
        val today = LocalDate.now(zone)
        val to = today.plusDays(HORIZON_DAYS.toLong())
        val agendas = agendaApi.observeRange(today, to).first()

        for (agenda in agendas) {
            val entry = agenda.entry
            when (entry.kind) {
                EntryKind.HABIT -> scheduleHabit(agenda, zone)
                EntryKind.SCHEDULE, EntryKind.EVENT -> scheduleTimed(
                    agenda = agenda,
                    kind = ReminderKind.BLOCK,
                    zone = zone,
                )
                EntryKind.TASK -> {
                    val mode = ReminderFireMode.fromAlarmStorage(entry.alarmMode)
                    if (mode != null) {
                        scheduleTimed(
                            agenda = agenda,
                            kind = ReminderKind.HABIT,
                            mode = mode,
                            zone = zone,
                        )
                    }
                }
                EntryKind.UNKNOWN -> Unit
            }
        }
    }

    private fun scheduleHabit(agenda: AgendaModel, zone: ZoneId) {
        val entry = agenda.entry
        val mode = ReminderFireMode.fromAlarmStorage(entry.alarmMode) ?: return
        val timeOfDay = entry.startTime ?: return
        val ahead = entry.reminderMinutesBefore ?: 0
        var trigger = LocalDateTime.of(agenda.date, timeOfDay).minusMinutes(ahead.toLong())
        if (trigger.toLocalDate().isBefore(agenda.date) && ahead > 0) {
            trigger = LocalDateTime.of(agenda.date, timeOfDay)
        }
        val epochMs = trigger.atZone(zone).toInstant().toEpochMilli()
        scheduler.schedule(
            ReminderRequest(
                kind = ReminderKind.HABIT,
                entityId = entry.id,
                epochDay = agenda.date.toEpochDay(),
                triggerAtEpochMs = epochMs,
                title = entry.title,
                mode = mode,
            ),
        )
    }

    private fun scheduleTimed(
        agenda: AgendaModel,
        kind: ReminderKind,
        zone: ZoneId,
        mode: ReminderFireMode = ReminderFireMode.NOTIFICATION,
    ) {
        val entry = agenda.entry
        val start = entry.startTime ?: return
        val trigger = LocalDateTime.of(agenda.date, start)
        val epochMs = trigger.atZone(zone).toInstant().toEpochMilli()
        scheduler.schedule(
            ReminderRequest(
                kind = kind,
                entityId = entry.id,
                epochDay = agenda.date.toEpochDay(),
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