package app.lade.calendar.domain

enum class FeedSource(val storage: String) {
    SCHEDULE("schedule"),
    MANUAL("manual"),
    HABIT("habit"),
    HEALTH("health"),
    CALENDAR("calendar"),
    ;

    companion object {
        fun fromStorage(value: String?): FeedSource =
            entries.find { it.storage == value } ?: MANUAL
    }
}