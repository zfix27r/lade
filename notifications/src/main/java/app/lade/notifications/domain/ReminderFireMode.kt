package app.lade.notifications.domain

enum class ReminderFireMode {
    NOTIFICATION,
    ALARM,
    ;

    companion object {
        fun fromAlarmStorage(value: String): ReminderFireMode? = when (value) {
            "notification" -> NOTIFICATION
            "alarm" -> ALARM
            else -> null
        }
    }
}