package app.lade.agenda.api.goal

enum class GoalUnit(val storage: String) {
    LAP("lap"),
    M("m"),
    KM("km"),
    KG("kg"),
    MIN("min"),
    HOUR("hour"),
    SET("set"),
    UNKNOWN("unknown"),
    ;

    companion object {
        fun fromStorage(value: String?): GoalUnit =
            entries.find { it.storage == value } ?: UNKNOWN
    }
}