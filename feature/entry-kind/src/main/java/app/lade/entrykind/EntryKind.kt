package app.lade.entrykind

enum class EntryKind(val storage: String) {
    TASK("task"),
    EVENT("event"),
    HABIT("habit"),
    SCHEDULE("schedule"),
    UNKNOWN("unknown"),
}