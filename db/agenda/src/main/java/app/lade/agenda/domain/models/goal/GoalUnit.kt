package app.lade.agenda.domain.models.goal

enum class GoalUnit(val storage: String) {
    LAP("lap"),
    M("m"),
    KM("km"),
    KG("kg"),
    MIN("min"),
    HOUR("hour"),
    SET("set"),
    UNKNOWN("unknown"),
}