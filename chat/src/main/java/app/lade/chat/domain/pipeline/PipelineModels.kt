package app.lade.chat.domain.pipeline

import app.lade.chat.domain.model.ChatDictEntry
import app.lade.entry.domain.models.Entry
import java.time.LocalDate

data class ParseContext(
	val raw: String,
	val normalized: String = "",
	val tokens: List<String> = emptyList(),
	val today: LocalDate = LocalDate.now(),
	val dicts: List<ChatDictEntry> = emptyList(),
	val entries: List<Entry> = emptyList(),
	val dayPartClock: app.lade.temporal.domain.DayPartClock =
		app.lade.temporal.domain.DayPartClock.DEFAULT,
	val intent: IntentResult? = null,
	val slots: SlotsResult? = null,
	val tail: TailResult? = null,
)

fun interface ParseStage {
	suspend fun process(ctx: ParseContext): ParseContext
}
