enum class CalendarBusySource(val storage: String) {
    SCHEDULE("schedule"),
    MANUAL("manual"),
    HEALTH("health"),
    CALENDAR("calendar"),
    ;

    companion object {
        fun fromStorage(value: String?): CalendarBusySource =
            entries.find { it.storage == value } ?: SCHEDULE
    }
}