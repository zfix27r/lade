package app.lade.agenda.api.log

enum class LogOrigin(val storage: String) {
    CHAT("chat"),
    CALENDAR("calendar"),
    AGENDA("agenda"),
    WIDGET("widget"),
    MANUAL("manual"),
    UNKNOWN("unknown"),
    ;

    companion object {
        fun fromStorage(value: String?): LogOrigin =
            entries.find { it.storage == value } ?: UNKNOWN
    }
}