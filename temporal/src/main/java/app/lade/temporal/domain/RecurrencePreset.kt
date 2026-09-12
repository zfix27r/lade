package app.lade.temporal.domain

enum class RecurrencePreset {
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