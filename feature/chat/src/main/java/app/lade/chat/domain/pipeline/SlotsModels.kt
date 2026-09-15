package app.lade.chat.domain.pipeline

/**
 * One extracted fact from intent rest tokens.
 * [key] is "value" for bare amount+unit, or a metric name (e.g. "пульс").
 */
data class SlotFact(
	val key: String,
	val amount: Double,
	val unit: String? = null,
)

data class SlotsResult(
	val facts: List<SlotFact> = emptyList(),
	val leftover: List<String> = emptyList(),
)

/** Orchestrator draft after normalize → intent → slots → tail. */
data class ParseDraft(
	val intent: IntentResult,
	val slots: SlotsResult = SlotsResult(),
	val tail: TailResult,
)
