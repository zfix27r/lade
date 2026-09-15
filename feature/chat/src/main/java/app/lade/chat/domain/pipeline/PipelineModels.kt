package app.lade.chat.domain.pipeline

import app.lade.chat.domain.model.ChatDictEntry
import app.lade.daypart.domain.DayPartClock
import app.lade.agenda.api.entry.EntryModel
import java.time.LocalDate

data class ParseContext(
    val raw: String,
    val normalized: String = "",
    val tokens: List<String> = emptyList(),
    val today: LocalDate = LocalDate.now(),
    val dicts: List<ChatDictEntry> = emptyList(),
    val entries: List<EntryModel> = emptyList(),
    val dayPartClock: DayPartClock =
		DayPartClock.DEFAULT,
    val intent: IntentResult? = null,
    val slots: SlotsResult? = null,
    val tail: TailResult? = null,
)

fun interface ParseStage {
	suspend fun process(ctx: ParseContext): ParseContext
}
