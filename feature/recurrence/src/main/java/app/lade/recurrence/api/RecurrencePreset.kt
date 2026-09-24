package app.lade.recurrence.api

enum class RecurrencePreset {
    None,
    Daily,
    Weekdays,
    Weekly,
    EveryNDays,
    Monthly,
    Yearly,
    ;

    companion object {
        val HABIT: List<RecurrencePreset> = entries
        val SCHEDULE: List<RecurrencePreset> = listOf(Daily, Weekdays, Weekly)
    }
}