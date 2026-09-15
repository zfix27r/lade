package app.lade.notifications.domain

data class ReminderRequest(
    val kind: ReminderKind,
    val entityId: Long,
    val epochDay: Long,
    val triggerAtEpochMs: Long,
    val title: String,
    val mode: ReminderFireMode,
)