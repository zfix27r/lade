package app.lade.draft.internal.chiper

internal enum class BarRecurrencePreset(
    val rrule: String?,
) {
    NONE(null),
    DAILY("RRULE:FREQ=DAILY"),
    WEEKDAYS("RRULE:FREQ=WEEKLY;BYDAY=MO,TU,WE,TH,FR"),
    WEEKENDS("RRULE:FREQ=WEEKLY;BYDAY=SA,SU"),
    WEEKLY("RRULE:FREQ=WEEKLY"),
    MONTHLY("RRULE:FREQ=MONTHLY"),
    YEARLY("RRULE:FREQ=YEARLY"),
    ;

    companion object {
        fun fromRrule(rrule: String?): BarRecurrencePreset =
            entries.find { it.rrule == rrule } ?: NONE
    }
}