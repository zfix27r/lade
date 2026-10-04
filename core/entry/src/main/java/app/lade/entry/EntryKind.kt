package app.lade.entry

enum class EntryKind(val storage: String) {
    NOTE("note"),
    TASK("task"),
    EVENT("event"),
    HABIT("habit"),
    SCHEDULE("schedule"),
    ;

    companion object {
        fun fromStorage(value: String?): EntryKind? =
            entries.find { it.storage == value }
    }
}