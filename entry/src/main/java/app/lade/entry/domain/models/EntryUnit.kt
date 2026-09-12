package app.lade.entry.domain.models

enum class EntryUnit(val storage: String) {
    KM("km"),
    MIN("min"),
    REPS("reps"),
    ;

    companion object {
        fun fromStorageOrNull(value: String?): EntryUnit? =
            entries.find { it.storage == value }
    }
}