package app.lade.entry

enum class EntryKind(val storage: String) {
    NOTE("note"),
    TASK("task"),
    EVENT("event"),
    HABIT("habit"),
    SCHEDULE("schedule"),
}