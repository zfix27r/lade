package app.lade.draftdata

import java.time.LocalTime

data class DraftAlarm(
    val raw: String? = null,
    val time: LocalTime,
    val mode: String = "sound",
)