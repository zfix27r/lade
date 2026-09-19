package app.lade.draft.internal.bar.conf

internal enum class DraftRecurrencePreset(
    val rrule: String?,
) {
    NONE(null),
    DAILY("RRULE:FREQ=DAILY"),
    WEEKLY("RRULE:FREQ=WEEKLY"),
    MONTHLY("RRULE:FREQ=MONTHLY"),
    YEARLY("RRULE:FREQ=YEARLY"),
    ;

    fun displayName(): String = when (this) {
        NONE -> "Не повторять"
        DAILY -> "Каждый день"
        WEEKLY -> "Каждую неделю"
        MONTHLY -> "Каждый месяц"
        YEARLY -> "Каждый год"
    }

    companion object {
        fun fromRrule(rrule: String?): DraftRecurrencePreset =
            entries.find { it.rrule == rrule } ?: NONE
    }
}