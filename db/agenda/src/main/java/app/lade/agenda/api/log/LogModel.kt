package app.lade.agenda.api.log

data class LogModel(
	val id: Long = 0,
	val entryId: Long,
	val epochDay: Long,
	val goalId: Long? = null,
	val name: String? = null,
	val unit: String? = null,
	val plannedAmount: Int? = null,
	val plannedRepeat: Int? = null,
	val plannedWeight: Double? = null,
	val actualAmount: Int? = null,
	val actualRepeat: Int? = null,
	val actualWeight: Double? = null,
	val origin: LogOrigin = LogOrigin.UNKNOWN,
	val createdAtEpochMs: Long,
)