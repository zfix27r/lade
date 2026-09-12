package app.lade.entry.domain.models

enum class EntryKind(val storage: String) {
    TASK("task"),
    EVENT("event"),
    HABIT("habit"),
    SCHEDULE("schedule"),
    ;

    companion object {
        fun fromStorage(value: String): EntryKind =
            entries.find { it.storage == value } ?: TASK
    }
}