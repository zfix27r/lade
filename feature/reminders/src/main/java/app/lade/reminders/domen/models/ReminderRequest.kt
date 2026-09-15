package app.lade.reminders.domen.models

import app.lade.notifications.domain.ReminderFireMode
import app.lade.notifications.domain.ReminderKind

data class ReminderRequest(
    val kind: ReminderKind,
    val entityId: Long,
    val epochDay: Long,
    val triggerAtEpochMs: Long,
    val title: String,
    val mode: ReminderFireMode,
)