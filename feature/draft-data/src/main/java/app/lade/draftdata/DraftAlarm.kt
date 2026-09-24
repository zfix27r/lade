package app.lade.draftdata

import java.time.LocalTime

data class DraftAlarm(
    val time: LocalTime,
    val mode: String = "sound",
)